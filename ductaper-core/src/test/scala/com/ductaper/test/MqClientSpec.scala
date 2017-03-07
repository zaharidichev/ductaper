package com.ductaper.test

import com.ductaper.core.channel.ChannelWrapper
import com.ductaper.core.client.MQClientImpl
import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.exchange.Exchange
import com.ductaper.core.message.{Message, MessagePayload, MessageProps}
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}
import com.ductaper.core.serialization.MessageConverter
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec}
import utils.Execution.currentThreadExecutionContext

/**
 * Created by zahari on 05/03/2017.
 */
class MqClientSpec extends FlatSpec with MockFactory{


  val chann = mock[ChannelWrapper]
  val connection = mock[ConnectionWrapper]
  val converter = mock[MessageConverter]


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
    client.send(data, route, props)(converter,currentThreadExecutionContext)

  }



}
