/*
package com.ductaper.test

import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.message.Key.ContentType
import com.ductaper.core.channel.{ChannelManager, ChannelWrapper}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.impl.AMQImpl.Queue.DeclareOk
import com.rabbitmq.client.{Channel, Consumer}
import com.ductaper.core.exchange.Exchange
import com.ductaper.core.message.{Message, MessagePayload, MessageProps}
import com.ductaper.core.route.{Queue, RoutingKey}

/**
 * Created by zahari on 06/02/2017.
 */
class ChannelManagerSpec extends WordSpecLike with Matchers with MockFactory {

  "ChannelManager" when {
    val channelMock = mock[Channel]
    val eventListenerMock: SystemEvent ⇒ Unit = mockFunction[SystemEvent, Unit]
    val consumerMock = mockFunction[Message, Unit]
    val channelOwner: ChannelWrapper = new ChannelManager(channelMock, eventListenerMock)
    val QUEUE_NAME: String = "SAMPLE_QUEUE"
    val EXCHANGE_NAME: String = "SAMPLE_EXCHANGE"
    val ROUTING_KEY_NAME: String = "ROUTING_KEY"

    "addAutoAckConsumer" should {

      "check that the queue exists and then add a consumer with auto ack set to true" in {
        (channelMock.basicConsume: (String, Boolean, Consumer) ⇒ String) expects (QUEUE_NAME, true, *)
        channelOwner.addAutoAckConsumer(Queue(QUEUE_NAME), consumerMock)
      }

    }

    "queueDeclare" should {

      "be able to passively declare a queue with QueuePassive" in {
        (channelMock.queueDeclarePassive: (String) ⇒ AMQP.Queue.DeclareOk) expects (QUEUE_NAME) returning new DeclareOk(QUEUE_NAME, 1, 1)
        channelOwner.declareQueue(QueuePassive(QUEUE_NAME))
      }

      "be able to passively declare a queue with QueueDeclared" in {
        (channelMock.queueDeclarePassive: (String) ⇒ AMQP.Queue.DeclareOk) expects (QUEUE_NAME) returning new DeclareOk(QUEUE_NAME, 1, 1)
        channelOwner.declareQueue(QueueDeclared(QUEUE_NAME))
      }

    }

    "queueBind" should {

      "call queue bind on the underlying java channel" in {
        val declaredQueue = QueueDeclared(QUEUE_NAME)
        val exchange = Exchange(EXCHANGE_NAME)
        val routingKey = RoutingKey(ROUTING_KEY_NAME)

        (channelMock.queueBind: (String, String, String) ⇒ AMQP.Queue.BindOk) expects (declaredQueue.name, exchange.name, routingKey.name)
        channelOwner.queueBind(declaredQueue, exchange, routingKey)
      }

    }

    "close" should {
      "close the undelying channel" in {
        (channelMock.close _) expects ()
        channelOwner.close()
      }

    }

  }

}*/
