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
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

/**
  * Created by zahari on 20/02/2017.
  */

  object Sample2 extends App {

    val conn = ConnectionWrapper.getConnection()
    val client = new MQClientImpl(conn)

    val timeout = Duration(5,TimeUnit.SECONDS)
    val routing = SampleController.SAMPLE_RPC_QUEUE.routingData
    val response: Future[Try[Response]] = client.sendAndReceive[Request,Response](Request("myCommand","yourCommand"),routing,MessageProps(),timeout)


   response.foreach(x => x match {
      case Success(response) => println("The response is " + response)
      case Failure(t) => println("The error is " + t)
    })


}
