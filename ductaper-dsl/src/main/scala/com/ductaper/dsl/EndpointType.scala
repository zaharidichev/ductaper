package com.ductaper.dsl

/**
  * Created by zahari on 08/02/2017.
  */
sealed trait EndpointType

trait BroadcastEndpointType extends EndpointType
trait UnicastEndpointType extends EndpointType

object EndpointTypes {
  // scalastyle:off
  case object unicast_endpoint extends UnicastEndpointType
  case object broadcast_endpoint extends BroadcastEndpointType
  // scalastyle:on
}
