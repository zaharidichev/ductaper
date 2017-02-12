package com.ductaper.core.dsl

/**
  * Created by zahari on 12/02/2017.
  */
object RouteConstants {
    val AUTH_ROUTE_BROADCAST =  BroadCastEndpointRoute("authQueue","authExchange")
    val AUTH_ROUTE_UNICAST = UnicastEndpointRoute("authQueue","authExchange")
}
