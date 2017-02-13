package com.ductaper

import java.util.concurrent.TimeUnit

import com.ductaper.core.message.DeliveryMode.Persistent
import com.ductaper.core.message.Key.{ContentType, CorrelationId, ReplyTo}
import com.ductaper.core.connection.ReconnectionStrategy.SimpleReconnectionStrategy
import com.ductaper.core.connection.{ConnectionManager, ConnectionWrapper}
import com.ductaper.core.container.{DuctaperRouteResult, MessageProcessingContainer}
import com.ductaper.core.dsl.EndpointTypes.{broadcast_endpoint, unicast_endpoint}
import com.ductaper.core.message.{Key, Message, MessagePayload, MessageProps}
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}

import scala.language.implicitConversions
import com.ductaper.core.serialization.JsonMessageConvertor.converter
import com.ductaper.core.serialization.JsonMessageConvertor._
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

    unicast_endpoint at SAMPLE_RPC_QUEUE takes input[Request] returns
      {
        request => Response("Computed response for command " + request.command + " and arguments " + request.arguments)
      }





  val connection = ConnectionWrapper.builder("amqp://guest:guest@localhost:5672", ConnectionManager.apply)
    .connectionTimeout(5000)
    .reconnectionStrategy(SimpleReconnectionStrategy(new FiniteDuration(10, TimeUnit.SECONDS))).build()
  val registrator = new MessageProcessingContainer(connection = connection)
  registrator.processEndpointDefinition(e)

}
