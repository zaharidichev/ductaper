package com.ductaper.test

import java.util.concurrent.TimeUnit

import com.ductaper.core.client.MQClientImpl
import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.message.MessageProps
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}
import com.ductaper.dsl.container.DefaultEndpointDefinitionProcessor
import com.rabbitmq.client.AMQP.Exchange
import com.ductaper.core.serialization.JsonMessageConverter.converter


import scala.concurrent._
import ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by zahari on 20/02/2017.
  */

  object Sample2 extends App {

    val conn = ConnectionWrapper.getConnection()
    val client = new MQClientImpl(conn)

    val routing = SampleController.SAMPLE_RPC_QUEUE.routingData
    val response: Future[Response] = client.sendAndReceive[Request,Response](Request("myCommand","yourCommand"),routing,MessageProps(),TimeUnit.DAYS)
    response.onComplete( x => {
      print(x.get)
    })


}
