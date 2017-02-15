package com.ductaper.core.channel

import com.ductaper._
import com.ductaper.core.CloseCapable
import com.ductaper.core.exchange.{Exchange, ExchangeType}
import com.ductaper.core.message.Message
import com.ductaper.core.route.{Binding, BrokerRoutingData, Queue, RoutingKey}

import scala.util.Try

/**
 * Created by zahari on 06/02/2017.
 */
trait ChannelWrapper extends CloseCapable {
  def declareExchange(exchange:Exchange): Try[Exchange]
  def declareQueue(queueDeclare: Queue): Try[Queue]
  def queueBind(queue: Queue, exchange: Exchange, routingKey: RoutingKey): Try[Binding]
  def send(route: BrokerRoutingData, message: Message): Unit
  def addAutoAckConsumer(queue: Queue, consumer: Message ⇒ Unit): CloseCapable
}