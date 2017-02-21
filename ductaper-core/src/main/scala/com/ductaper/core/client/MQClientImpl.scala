package com.ductaper.core.client

import com.ductaper.core.channel.ChannelWrapper
import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.exchange.Exchange
import com.ductaper.core.message.{Message, MessageProps}
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}
import com.ductaper.core.serialization.MessageConverter
import com.sun.javafx.scene.layout.region.Margins.Converter

import scala.concurrent.duration.TimeUnit
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by zahari on 20/02/2017.
  */
class MQClientImpl(private val connectionWrapper: ConnectionWrapper) extends MQClient{


  type OnChannelFunc = ChannelWrapper => Unit

  def send[T](data: T, routingData: BrokerRoutingData, messageProps: MessageProps)
             (implicit converter: MessageConverter): Unit = {
    Future {
        execOnChannel(chan => {
          val payload = converter.toPayload(data)
          chan.send(routingData, Message(messageProps, payload))
        })
      }
  }



  // This is super rudimentary for now, just to see whether the basic logic works at all...
  override def sendAndReceive[T,R](data:T, routingData: BrokerRoutingData,
                          messageProps: MessageProps,
                          timeout: TimeUnit)
                         (implicit converter: MessageConverter,responseManifest:Manifest[R]): Future[R] = {

    val chann = connectionWrapper.newChannel()
    val callBackQueue = chann.declareQueue.get

    val messagePayload = converter.toPayload(data)
    val messageProps = MessageProps().replyTo(BrokerRoutingData(Exchange.DEFAULT_EXCHANGE,RoutingKey(callBackQueue.name.getOrElse(""))))
    val messageToSend = Message(messageProps,messagePayload)

    val promise: Promise[R] = Promise()


    val conusmerCallBack: Message => Unit = m => {
      val response: R = converter.fromPayload(m.body)
      promise.success(response)
    }
    chann.addAutoAckConsumer(callBackQueue, conusmerCallBack)
    chann.send(routingData,messageToSend)
    promise.future
  }


  private def execOnChannel(toExec:OnChannelFunc): Unit = {
    val chan = connectionWrapper.newChannel()
    toExec(chan)
    chan.close()
  }

}
