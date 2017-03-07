package com.ductaper.dsl

import com.ductaper.core.exchange.{DirectExchange, Exchange, FanoutExchange}
import com.ductaper.core.route.{BrokerRoutingData, QueueDeclare, RoutingKey}

/**
  * Created by zahari on 08/02/2017.
  */


trait EndpointRoute{
  def queue:QueueDeclare
  def exchange:Exchange
  def routingData: BrokerRoutingData = BrokerRoutingData(exchange,RoutingKey(queue.nameOrEmpty))
}

case class UnicastEndpointRoute (override val queue:QueueDeclare, override val exchange:DirectExchange) extends EndpointRoute
case class BroadCastEndpointRoute(override val exchange:FanoutExchange) extends EndpointRoute{
  override val queue: QueueDeclare = QueueDeclare()
}

