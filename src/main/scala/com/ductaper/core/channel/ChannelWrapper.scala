package com.ductaper.core.channel

import com.ductaper._
import com.ductaper.core.CloseCapable
import com.ductaper.core.exchange.{Exchange, ExchangeType}
import com.ductaper.core.message.Message
import com.ductaper.core.route.{BrokerRoutingData, Queue, QueueDeclared, RoutingKey}

/**
 * Created by zahari on 06/02/2017.
 */
trait ChannelWrapper extends CloseCapable {
  def declareExchange(exchangeName: String, exchangeType: ExchangeType, durable: Boolean, autoDelete: Boolean, args: Map[String, AnyRef]): Exchange
  def declareExchangePassive(exchangeName: String): Exchange
  def declareQueue(queueDeclare: Queue): QueueDeclared
  def queueBind(queue: QueueDeclared, exchange: Exchange, routingKey: RoutingKey): Unit
  def send(route: BrokerRoutingData, message: Message): Unit
  def addAutoAckConsumer(queue: Queue, consumer: Message ⇒ Unit): CloseCapable
}