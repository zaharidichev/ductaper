package com.ductaper.core.dsl

import com.ductaper.core.exchange.{DirectExchange, Exchange, FanoutExchange}
import com.ductaper.core.route.Queue

/**
  * Created by zahari on 08/02/2017.
  */


trait EndpointRoute{
  def queue:Queue
  def exchange:Exchange
}

case class UnicastEndpointRoute (override val queue:Queue, override val exchange:DirectExchange) extends EndpointRoute
case class BroadCastEndpointRoute(override val queue:Queue, override val exchange:FanoutExchange) extends EndpointRoute

