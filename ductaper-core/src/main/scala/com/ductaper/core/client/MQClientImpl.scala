package com.ductaper.core.client

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Try}
import com.ductaper.core.channel.ChannelWrapper
import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.error.MqTimeoutException
import com.ductaper.core.exchange.Exchange
import com.ductaper.core.message.{Message, MessageProps}
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}
import com.ductaper.core.serialization.MessageConverter

/**
  * Created by zahari on 20/02/2017.
  */
class MQClientImpl(private val connectionWrapper: ConnectionWrapper) extends MQClient {

  type OnChannelFunc = ChannelWrapper => Unit

  def send[T](data: T, routingData: BrokerRoutingData, messageProps: MessageProps)
             (implicit converter: MessageConverter, executionContext: ExecutionContext): Unit = {
    Future {
      execOnChannel(chan => {
        val payload = converter.toPayload(data)
        chan.send(routingData, Message(messageProps, payload))
      })
    }
  }

  private def addTimeoutHookToPromise[R](p: Promise[Try[R]], duration: Duration)(implicit executionContext: ExecutionContext) = {
    Future {
      Thread.sleep(duration.toMillis)
      p.trySuccess(Failure(new MqTimeoutException("Response took more than " + duration.toMillis + " milliseconds to arrive")))
    }
  }

  // This is super rudimentary for now, just to see whether the basic logic works at all...
  // Currently we are creating new channel and a consumer for each call to this method.
  // It would be great if we can pool these somehow to avoid the overhead
  override def sendAndReceive[T, R](data: T, routingData: BrokerRoutingData,
                                    messageProps: MessageProps,
                                    timeout: Duration)
                                   (implicit converter: MessageConverter,
                                    responseManifest: Manifest[R],
                                    executionContext: ExecutionContext): Future[Try[R]] = {

    val chann = connectionWrapper.newChannel()
    val callBackQueue = chann.declareQueue.get

    val messagePayload = converter.toPayload(data)
    val messagePropsWithReplyToRoute = messageProps.replyTo(BrokerRoutingData(Exchange.DEFAULT_EXCHANGE, RoutingKey(callBackQueue.name.getOrElse(""))))
    val messageToSend = Message(messagePropsWithReplyToRoute, messagePayload)

    val promise: Promise[Try[R]] = Promise()

    val conusmerCallBack: Message => Unit = m => {
      val result: Try[R] = Try(converter.fromPayload(m.body))
      promise.trySuccess(result)
    }
    val consumerHandle = chann.addAutoAckConsumer(callBackQueue, conusmerCallBack)
    chann.send(routingData, messageToSend)

    addTimeoutHookToPromise(promise, timeout)
    val futureResult = promise.future

    // when finished clean up the resources
    futureResult.onComplete(_ => {
      consumerHandle.close()
      chann.close()
    })

    promise.future
  }

  private def execOnChannel(toExec: OnChannelFunc): Unit = {
    val chan = connectionWrapper.newChannel()
    toExec(chan)
    chan.close()
  }

}
