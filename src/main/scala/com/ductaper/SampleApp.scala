package com.ductaper

import java.util.concurrent.TimeUnit

import com.ductaper.core.connection.ReconnectionStrategy.SimpleReconnectionStrategy
import com.ductaper.core.connection.{ConnectionManager, ConnectionWrapper}
import com.ductaper.core.container.{DefaultEndpointDefinitionProcessor}
import com.ductaper.core.dsl.EndpointTypes.{broadcast_endpoint, unicast_endpoint}

import scala.language.implicitConversions
import com.ductaper.core.serialization.JsonMessageConverter$.converter
import com.ductaper.core.dsl.EndpointImplicits._
import com.ductaper.core.dsl.RouteConstants._
import scala.concurrent.duration.FiniteDuration

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
case class Request(command: String, arguments: String)
case class Response(result: String)

object SampleApp extends App {

  val e =

    unicast_endpoint at SAMPLE_RPC_QUEUE takes input[Request] consumes {
        (x) => println(x.arguments)

      }

  broadcast_endpoint at AUTH_ROUTE_BROADCAST calls {
    () => 1 + 1
  }

  val connection = ConnectionWrapper.builder("amqp://guest:guest@localhost:5672", ConnectionManager.apply)
    .connectionTimeout(5000)
    .reconnectionStrategy(SimpleReconnectionStrategy(new FiniteDuration(10, TimeUnit.SECONDS))).build()
  val registrator = new DefaultEndpointDefinitionProcessor(connection = connection)
  registrator.processEndpointDefinitions(Seq(e))

}
