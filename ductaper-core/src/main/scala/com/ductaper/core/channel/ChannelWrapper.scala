package com.ductaper.core.channel

import scala.util.Try
import com.ductaper.core.exchange.Exchange
import com.ductaper.core.message.Message
import com.ductaper.core.misc.CloseCapable
import com.ductaper.core.route._

/**
  * Created by zahari on 06/02/2017.
  */
trait ChannelWrapper extends CloseCapable {
  def declareExchange(exchange: Exchange): Try[Exchange]
  def declareQueue(queueDeclare: QueueDeclare): Try[Queue]
  def declareQueue: Try[Queue]
  def queueBind(queue: Queue, exchange: Exchange, routingKey: RoutingKey): Try[Binding]
  def send(route: BrokerRoutingData, message: Message): Unit
  def addAutoAckConsumer(queue: Queue, consumer: Message => Unit): CloseCapable
}
