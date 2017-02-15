package com.ductaper

import java.util.concurrent.TimeUnit

import com.ductaper.core.connection.ReconnectionStrategy.SimpleReconnectionStrategy
import com.ductaper.core.connection.{ConnectionManager, ConnectionWrapper}
import com.ductaper.core.exchange.{DirectExchange, FanoutExchange}
import com.ductaper.core.route.Queue
import com.ductaper.core.serialization.JsonMessageConverter
import com.ductaper.dsl.{BroadCastEndpointRoute, DuctaperController, UnicastEndpointRoute}
import com.ductaper.dsl.EndpointTypes.{broadcast_endpoint, unicast_endpoint}
import com.ductaper.dsl.EndpointImplicits._
import com.ductaper.dsl.container.DefaultEndpointDefinitionProcessor

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
case class Request(command: String, arguments: String)
case class Response(result: String)



object SampleController extends DuctaperController {

  val AUTH_ROUTE_BROADCAST =  BroadCastEndpointRoute(Queue(Some("auth.broadcast.queue")),FanoutExchange("auth.fanout.EXCHANGE"))
  val SAMPLE_RPC_QUEUE = UnicastEndpointRoute(Queue(Some("sample.rpc.queue")),DirectExchange("sample.rpc.exchange"))

  override val endpoints =

    {


      unicast_endpoint at SAMPLE_RPC_QUEUE takes input[Request] returns {
        (x) => Response("hahahaha")
      }


    } ~ {


      broadcast_endpoint at AUTH_ROUTE_BROADCAST calls {
        () => println(1 + 1)
      }


    } ~ {


      unicast_endpoint at SAMPLE_RPC_QUEUE calls {
        () => println(5 + 5)
      }


    }

}

object Sample extends App{

  implicit val converter = JsonMessageConverter.converter


  val connection = ConnectionWrapper
    .builder("amqp://guest:guest@localhost:5672", ConnectionManager.apply)
    .connectionTimeout(5000)
    .reconnectionStrategy(SimpleReconnectionStrategy(new FiniteDuration(10, TimeUnit.SECONDS))).build()
  DefaultEndpointDefinitionProcessor(connection).processEndpointDefinitions(SampleController.endpoints)

}
