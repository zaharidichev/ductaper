package com.ductaper.test

import java.util.concurrent.TimeUnit

import com.ductaper.core.channel.{ChannelWrapper, ConsumerHandle}
import com.ductaper.core.client.MQClientImpl
import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.error.MqTimeoutException
import com.ductaper.core.exchange.{DirectExchange, Exchange}
import com.ductaper.core.message.{Message, MessagePayload, MessageProps}
import com.ductaper.core.misc.CloseCapable
import com.ductaper.core.route.{BrokerRoutingData, Queue, QueueDeclare, RoutingKey}
import com.ductaper.core.serialization.MessageConverter
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, WordSpecLike}
import utils.Execution.currentThreadExecutionContext

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

/**
 * Created by zahari on 05/03/2017.
 */
class MqClientSpec extends FlatSpec with MockFactory {

  val timeout = 10

  val chann = mock[ChannelWrapper]
  val connection = mock[ConnectionWrapper]
  implicit val converter = mock[MessageConverter]


  val data = "sampleData"
  val payload = MessagePayload(data)
  val route = BrokerRoutingData(Exchange.DEFAULT_EXCHANGE, RoutingKey("key"))
  val props = MessageProps()
  val message = Message(props, payload)


  "send" should "create new channel, convert data to payload, send the message and close the channel" in {

    inSequence {

      (connection.newChannel _) expects() returning chann
      (converter.toPayload[String] _).expects(data).returning(payload)
      (chann.send: (BrokerRoutingData, Message) => Unit) expects(route, message) returning Unit
      (chann.close _) expects() returning Unit

    }

    val client = new MQClientImpl(connection)
    client.send(data, route, props)(converter, currentThreadExecutionContext)
  }



  "sendAndReceive" should "return a timeout failure after creating new channel, " +
                          "creating a callback queue, converting data to payload, " +
                          "adding auto ack consumer, sending request, closing " +
                          "consumer handle, and closing channel" in {

    val callBackQueue = QueueDeclare(Some("random name"))
    val propsOfTheMessage = MessageProps().replyTo(BrokerRoutingData(Exchange.DEFAULT_EXCHANGE,RoutingKey(callBackQueue.name.getOrElse(""))))
    val messageToBeSend = Message(propsOfTheMessage,payload)
    val mockConsumerHandler = mock[ConsumerHandle]

    inSequence {

      (connection.newChannel _) expects() returning chann
      (chann.declareQueue _) expects() returning Try(callBackQueue)
      (converter.toPayload[String] _).expects(data).returning(payload)

      (chann.addAutoAckConsumer: (Queue, Message => Unit) => CloseCapable) expects (callBackQueue, *) returning mockConsumerHandler
      (chann.send: (BrokerRoutingData, Message) => Unit) expects(route, messageToBeSend) returning Unit
      (mockConsumerHandler.close _) expects() returning Unit
      (chann.close _) expects() returning Unit

    }

    val client = new MQClientImpl(connection)
    client.sendAndReceive[String,String](data,route,props,Duration(timeout,TimeUnit.MILLISECONDS)).foreach( result => {
      result match {
        case Success(_) => fail()
        case Failure(ex) => assert(ex.isInstanceOf[MqTimeoutException])
      }
    })

  }

}
