package com.ductaper.test

import com.ductaper.core.channel.{ChannelManager, ChannelWrapper}
import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.exchange.DirectExchange
import com.ductaper.core.message.Message
import com.ductaper.core.route.{QueueDeclare, QueuePassive, RoutingKey}
import com.ductaper.core.thinwrappers.ChannelThinWrapper
import com.rabbitmq.client.AMQP.{Exchange => RabbitExchange}
import com.rabbitmq.client.impl.AMQImpl
import com.rabbitmq.client.impl.AMQImpl.Queue.DeclareOk
import com.rabbitmq.client.{AMQP, Consumer}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}

/**
  * Created by zahari on 06/02/2017.
  */
class ChannelManagerSpec extends WordSpecLike with Matchers with MockFactory {

  //noinspection ScalaStyle
  class MockableThinWrapper extends ChannelThinWrapper(null)

  "ChannelManager" when {
    val channelMock = mock[MockableThinWrapper]
    val eventListenerMock: SystemEvent => Unit = mockFunction[SystemEvent, Unit]
    val consumerMock = mockFunction[Message, Unit]

    val channelOwner: ChannelWrapper = new ChannelManager(channelMock, eventListenerMock)
    val QUEUE_NAME: String = "SAMPLE_QUEUE"
    val EXCHANGE_NAME: String = "SAMPLE_EXCHANGE"
    val ROUTING_KEY_NAME: String = "ROUTING_KEY"

    "addAutoAckConsumer" should {

      "call the queue declare on the underlying thin wrapper if the queue is of type QueueDeclare" in {
        val queue = QueueDeclare(Some(QUEUE_NAME))

        (channelMock.native _) expects ()

        (channelMock.basicConsume: (String, Boolean, Consumer) => String) expects (queue.name.get, true, *)

        (channelMock.queueDeclare: (String, Boolean, Boolean, Boolean, Map[String, AnyRef]) => AMQP.Queue.DeclareOk) expects
          (queue.name.get, queue.durable, queue.exclusive, queue.autoDelete, queue.args) returning
          new DeclareOk(QUEUE_NAME, 1, 1)

        channelOwner.addAutoAckConsumer(queue, consumerMock)
      }

      "should not call the queue declare on the underlying thin wrapper if the queue is of type QueuePassive" in {
        val queue = QueuePassive(Some(QUEUE_NAME))

        (channelMock.native _) expects ()
        (channelMock.basicConsume: (String, Boolean, Consumer) => String) expects (queue.name.get, true, *)
        channelOwner.addAutoAckConsumer(queue, consumerMock)
      }

    }

    "declareExchange" should {

      "call the underlying exchange declare on the thin wrapper" in {
        val exchange = DirectExchange(EXCHANGE_NAME)

        (channelMock.exchangeDeclare: (String, String, Boolean, Boolean, Map[String, AnyRef]) => RabbitExchange.DeclareOk) expects
          (exchange.name, exchange.exchangeType.name, exchange.durable, exchange.autoDelete, exchange.args) returning
          new AMQImpl.Exchange.DeclareOk()
        channelOwner.declareExchange(exchange)
      }
    }

    "declareQueue" should {

      "be able to actively declare a queue with QueueDeclare" in {
        val queue = QueueDeclare(Some(QUEUE_NAME))

        (channelMock.queueDeclare: (String, Boolean, Boolean, Boolean, Map[String, AnyRef]) => AMQP.Queue.DeclareOk) expects
          (queue.name.get, queue.durable, queue.exclusive, queue.autoDelete, queue.args) returning
          new DeclareOk(QUEUE_NAME, 1, 1)

        channelOwner.declareQueue(queue)
      }

      "when called without args should declare a server-named exclusive, autodelete, non-durable queue" in {

        (channelMock.queueDeclare _) expects () returning new DeclareOk(QUEUE_NAME, 1, 1)

        val resultingQueue = channelOwner.declareQueue
        assert(resultingQueue.get.isInstanceOf[QueuePassive])
        assert(resultingQueue.get.name.get == QUEUE_NAME)

      }

    }

    "queueBind" should {

      "call queue bind on the thin wrapper" in {
        val declaredQueue = QueueDeclare(Some(QUEUE_NAME))
        val exchange = DirectExchange(EXCHANGE_NAME)
        val routingKey = RoutingKey(ROUTING_KEY_NAME)

        (channelMock.queueBind: (String, String, String) => AMQP.Queue.BindOk) expects
          (declaredQueue.name.get, exchange.name, routingKey.name)

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

}
