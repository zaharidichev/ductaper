package com.ductaper.core.container

import com.ductaper.core.CloseCapable
import com.ductaper.core.channel.{ChannelWrapper, ConsumerHandle}
import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.dsl._
import com.ductaper.core.exchange.DirectExchange
import com.ductaper.core.message.Key.{CorrelationId, ReplyTo}
import com.ductaper.core.message.{Message, MessagePayload, MessageProps}
import com.ductaper.core.route.{BrokerRoutingData, Queue, RoutingKey}
import com.ductaper.core.serialization.MessageSerialization

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
 * Created by zahari on 07/02/2017.
 */
class MessageProcessingContainer(connection: ConnectionWrapper) extends CloseCapable {

  private val _adminChannel: ChannelWrapper = connection.newChannel()
  private val _consumerHandles = scala.collection.mutable.HashSet[CloseCapable]()

  def adminChannel: ChannelWrapper = _adminChannel
  def consumerHandles: mutable.HashSet[CloseCapable] = _consumerHandles

  def ensureBindingsArePresent(route: EndpointRoute): Try[Unit] = {
    for {
      exchange <- _adminChannel.declareExchange(route.exchange)
      queue    <- _adminChannel.declareQueue(route.queue)
    }
      yield      _adminChannel.queueBind(queue, exchange, RoutingKey(queue.name.getOrElse("")))
  }



  val defaultExchange = DirectExchange("")


  def processInputOutputEndpointDefinition[T, R](e: InputOutputEndpointDefinition[T, R])
                                                (implicit inputManifest: Manifest[T],
                                                 outputManifest: Manifest[R],
                                                 converter: MessageSerialization): Unit = {

    val consumerChannel = connection.newChannel()
    consumerChannel.addAutoAckConsumer(e.endpointRoute.queue, (message) => {
      val input = converter.deserializeFromPayload(message.body)(inputManifest)
      val output = e.functor(input)
      val replyTo  = message.messageProperties.getOrNull(ReplyTo).asInstanceOf[String]
      val messageToSend = Message(MessageProps(),converter.serializeToPayload(output))
      consumerChannel.send(BrokerRoutingData(defaultExchange,RoutingKey(replyTo)),messageToSend)
    })

  }

  def processNoInputOutputEndpointDefinition[R](e: NoInputOutputEndpointDefinition[R])(implicit outputManifest: Manifest[R]): Unit = {
    println("Endpoint at " + e.endpointRoute + " executing () => " + outputManifest)
  }

  def processInputNoOutputEndpointDefinition[T](e: InputNoOutputEndpointDefinition[T])(implicit inputManifest: Manifest[T]): Unit = {
    println("Endpoint at " + e.endpointRoute + " executing " + inputManifest + " => Unit")

  }

  def processNoInputNoOutputEndpointDefinition(e: NoInputNoOutputEndpointDefinition): Unit = {
    println("Endpoint at " + e.endpointRoute + " executing () => Unit")

  }

  def processEndpointRoutes(seq: Seq[EndpointDefinition])(implicit converter:MessageSerialization): Unit = {
    seq.foreach(endpointDefinition => {
      ensureBindingsArePresent(endpointDefinition.endpointRoute) match {
        case Success(_) => processEndpointDefinition(endpointDefinition)
        case Failure(error) => println("Cannot create endpoints due to " + error)
      }
    })
  }

  def processEndpointDefinition(e: EndpointDefinition)(implicit converter:MessageSerialization): Unit = {
    e match {
      case end: InputOutputEndpointDefinition[_, _] => {
        implicit val inputManifest = end.inputManifest
        implicit val outputManifest = end.outputManifest
        processInputOutputEndpointDefinition(end)
      }

      case end: NoInputOutputEndpointDefinition[_] => {
        implicit val outputManifest = end.outputManifest
        processNoInputOutputEndpointDefinition(end)
      }

      case end: InputNoOutputEndpointDefinition[_] => {
        implicit val inputManifest = end.inputManifest
        processInputNoOutputEndpointDefinition(end)
      }

      case end: NoInputNoOutputEndpointDefinition => processNoInputNoOutputEndpointDefinition(end)

    }

  }
  def addConsumer[V, R](queueDeclared: Queue, consumerFunction: V ⇒ R, deserializer: Message ⇒ V, serializer: R ⇒ Message): Unit = {
    synchronized {
      val channelForConsumer = connection.newChannel()
      val consumerHandle = channelForConsumer.addAutoAckConsumer(queueDeclared, message ⇒ {
        val payload = deserializer(message)
        val result = consumerFunction(payload)
      })

      consumerHandles += consumerHandle
    }
  }

  override def close(): Unit = {
    consumerHandles.foreach(_.close())
    adminChannel.close()
  }
}
