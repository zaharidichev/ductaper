package com.ductaper.core.dsl

import com.ductaper.core.exchange.{DirectExchange, FanoutExchange}
import com.ductaper.core.route.Queue


/**
  * Created by zahari on 12/02/2017.
  */
object RouteConstants {
    val AUTH_ROUTE_BROADCAST =  BroadCastEndpointRoute(Queue(Some("auth.broadcast.queue")),FanoutExchange("auth.fanout.EXCHANGE"))
    val SAMPLE_RPC_QUEUE = UnicastEndpointRoute(Queue(Some("sample.rpc.queue")),DirectExchange("sample.rpc.exchange"))
}
