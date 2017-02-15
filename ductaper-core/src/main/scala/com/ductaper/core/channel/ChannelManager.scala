package com.ductaper.core.channel

import java.util.Date

import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.message.Key.Timestamp
import com.ductaper._
import com.ductaper.core.CloseCapable
import com.ductaper.core.exchange.{Exchange, ExchangeType}
import com.ductaper.core.message.Message
import com.ductaper.core.route._
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client._

import scala.collection.JavaConverters
import scala.util.Try

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

class ChannelManager(chan: Channel, eventListener: SystemEvent ⇒ Unit) extends ChannelWrapper {

  override def declareExchange(exchange: Exchange): Try[Exchange] = {
    Try(chan.exchangeDeclare(
      exchange.name,
      exchange.exchangeType.name,
      exchange.durable,
      exchange.autoDelete,
      JavaConverters.mapAsJavaMap(exchange.args)
    )).map(_ => exchange)
  }

  override def declareQueue(queue: Queue): Try[Queue] = {
    Try(chan.queueDeclare(queue.name.getOrElse(""), queue.durable, queue.exclusive, queue.autoDelete, JavaConverters.mapAsJavaMap(queue.args))).map(_ => queue)
  }

  override def addAutoAckConsumer(queue: Queue, consumer: Message ⇒ Unit): CloseCapable = {
    val nameOfQueue = declareQueue(queue).get.name.getOrElse("")
    val nativeConsumer = new DefaultConsumer(chan) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        val mess: Message = Message(properties, body);
        consumer(mess)
      }
    }
    new ConsumerHandle(chan, chan.basicConsume(nameOfQueue, true, nativeConsumer))
  }

  override def queueBind(
    queue: Queue,
    exchange: Exchange,
    routingKey: RoutingKey
  ): Try[Binding] = Try(chan.queueBind(queue.name.getOrElse(""), exchange.name, routingKey.name)).map(_ => Binding(queue,exchange,routingKey))


  override def close(): Unit = chan.close()

  override def send(route: BrokerRoutingData, message: Message): Unit = {
    val basicProps: BasicProperties = (message.messageProperties.timestamp(new Date)).toJavaBasicProps
    sendViaChannel(route.exchange.name, route.routingKey.name, basicProps, message.body.toArray)
  }

  private def sendViaChannel(exchangeName: String, routingKey: String, props: BasicProperties, body: Array[Byte]) = {
    chan.basicPublish(exchangeName, routingKey, props, body)
  }
}
