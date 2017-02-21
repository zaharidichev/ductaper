package com.ductaper.test

import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.exchange.{DirectExchange, FanoutExchange}
import com.ductaper.core.route.{Queue, QueueDeclare}
import com.ductaper.core.serialization.JsonMessageConverter.converter
import com.ductaper.dsl.EndpointImplicits._
import com.ductaper.dsl.EndpointTypes.{broadcast_endpoint, unicast_endpoint}
import com.ductaper.dsl.container.DefaultEndpointDefinitionProcessor
import com.ductaper.dsl.{BroadCastEndpointRoute, DuctaperController, UnicastEndpointRoute}

import scala.language.implicitConversions


/**
  * @author Zahari Dichev <zaharidichev@gmail.com>.
  */
case class Request (command: String, arguments: String)
case class Response(result: String)



object SampleController extends DuctaperController {

  val AUTH_ROUTE_BROADCAST =  BroadCastEndpointRoute(QueueDeclare(Some("auth.broadcast.queue")),FanoutExchange("auth.fanout.EXCHANGE"))
  val SAMPLE_RPC_QUEUE = UnicastEndpointRoute(QueueDeclare(Some("sample.rpc.queue")),DirectExchange("sample.rpc.exchange"))

  override val endpoints =

      unicast_endpoint consumers 4 at SAMPLE_RPC_QUEUE takes input[Request] returns[Response] {
        (x) => {
          Response("hahahaha")
        }
      }






}





object Sample extends App{


  DefaultEndpointDefinitionProcessor(ConnectionWrapper.getConnection()).processEndpointDefinitions(SampleController.endpoints)

}
