package com.ductaper.core.dsl

/**
  * Created by zahari on 08/02/2017.
  */


sealed trait EndpointRoute{
  val queue:String
  val exchange:String
}

case class UnicastEndpointRoute (override val queue:String, override val exchange:String) extends EndpointRoute
case class BroadCastEndpointRoute(override val queue:String, override val exchange:String) extends EndpointRoute

