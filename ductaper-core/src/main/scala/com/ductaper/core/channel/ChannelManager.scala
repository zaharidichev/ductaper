package com.ductaper.core.channel

import java.util.Date
import scala.util.{Failure, Try}
import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.exchange.Exchange
import com.ductaper.core.message.Message
import com.ductaper.core.misc.CloseCapable
import com.ductaper.core.route._
import com.ductaper.core.thinwrappers.ChannelThinWrapper
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client._
import org.slf4j.LoggerFactory

 /*
  * @author Zahari Dichev <zaharidichev@gmail.com>.
  */
class ChannelManager(chan: ChannelThinWrapper, eventListener: SystemEvent => Unit) extends ChannelWrapper {

  val logger = LoggerFactory.getLogger(classOf[ChannelManager])

  override def declareExchange(exchange: Exchange): Try[Exchange] = {
    Try(
      chan.exchangeDeclare(
        exchange.name,
        exchange.exchangeType.name,
        exchange.durable,
        exchange.autoDelete,
        exchange.args
      )).map(_ => exchange)
  }

  override def declareQueue(queue: QueueDeclare): Try[Queue] = {
    Try(chan.queueDeclare(queue.name.getOrElse(""),
      queue.durable,
      queue.exclusive,
      queue.autoDelete,
      queue.args)).map(q => QueuePassive(Some(q.getQueue)))
  }

  private def ensureQueue(q: Queue): Try[Queue] = {
    q match {
      case declare: QueueDeclare => declareQueue(declare)
      case passive: QueuePassive => Try(passive)
    }
  }

  /*  Actively declare a server-named exclusive, autodelete, non-durable queue.
   * The name of the new queue is held in the "queue" field of the {@link com.rabbitmq.client.AMQP.Queue.DeclareOk} result.
   */
  override def declareQueue(): Try[Queue] = {
    Try(chan.queueDeclare()).map(q => QueuePassive(Option(q.getQueue)))
  }

  override def addAutoAckConsumer(queue: Queue, consumer: Message => Unit): CloseCapable = {
    val nameOfQueue = ensureQueue(queue).get.name.getOrElse("")

    val nativeConsumer = new DefaultConsumer(chan.native) {
      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]): Unit = {
        val mess: Message = Message(properties, body);
        Try(consumer(mess)) match {
          case Failure(t) => logger.error("Exception was thrown while executing method on consumer", t)
          case _ => ()
        }
      }
    }
    new ConsumerHandle(chan, chan.basicConsume(nameOfQueue, true, nativeConsumer))
  }

  override def queueBind(
      queue: Queue,
      exchange: Exchange,
      routingKey: RoutingKey
  ): Try[Binding] = Try(chan.queueBind(queue.name.getOrElse(""), exchange.name, routingKey.name))
    .map(_ => Binding(queue, exchange, routingKey))

  override def close(): Unit = chan.close()

  override def send(route: BrokerRoutingData, message: Message): Unit = {
    val basicProps: BasicProperties = (message.messageProperties.timestamp(new Date)).toJavaBasicProps
    sendViaChannel(route.exchange.name, route.routingKey.name, basicProps, message.body.toArray)
  }

  private def sendViaChannel(exchangeName: String, routingKey: String, props: BasicProperties, body: Array[Byte]) = {
    chan.basicPublish(exchangeName, routingKey, props, body)
  }
}
