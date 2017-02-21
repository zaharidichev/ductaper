package com.ductaper.dsl.container

import com.ductaper.core.channel.ChannelWrapper
import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.message.Key.ReplyTo
import com.ductaper.core.message.{Message, MessageProps}
import com.ductaper.core.misc.CloseCapable
import com.ductaper.core.route.{Binding, Queue, RoutingKey}
import com.ductaper.core.serialization.MessageConverter
import com.ductaper.dsl._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

/**
 * Created by zahari on 07/02/2017.
 */
class DefaultEndpointDefinitionProcessor(connection: ConnectionWrapper) extends CloseCapable with EndpointDefinitionProcessor {

  private val _logger = LoggerFactory.getLogger(classOf[DefaultEndpointDefinitionProcessor])
  private val _adminChannel: ChannelWrapper = connection.newChannel()
  private val _consumerHandles = scala.collection.mutable.HashSet[CloseCapable]()

  def adminChannel: ChannelWrapper = _adminChannel
  def consumerHandles: Set[CloseCapable] = _consumerHandles.toSet

  private def ensureBindingsArePresent(route: EndpointRoute): Try[Binding] = {
    {
      for {
        exchange <- _adminChannel.declareExchange(route.exchange)
        queue <- _adminChannel.declareQueue(route.queue)
      } yield _adminChannel.queueBind(queue, exchange, RoutingKey(queue.name.getOrElse("")))
    }.flatten

  }

  private def sendObjectAsResponse[T](objectToSend: T, request: Message, chan: ChannelWrapper)(implicit converter: MessageConverter) {
    lazy val messageToSend = Message(MessageProps(), converter.toPayload(objectToSend))
    request.property(ReplyTo) match {
      case Some(route) => chan.send(route, messageToSend)
      case None => _logger.error("Request message is missing ReplyTo property. Unable to send response")
    }
  }

  private def processInputOutputEndpointDefinition[T, R](e: InputOutputEndpointDefinition[T, R])(implicit
    inputManifest: Manifest[T],
    outputManifest: Manifest[R],
    converter: MessageConverter): CloseCapable = {

    val consumerChannel = connection.newChannel()
    consumerChannel.addAutoAckConsumer(e.endpointRoute.queue, (message) => {
      val input = converter.fromPayload(message.body)(inputManifest)
      val output = e.functor(input)
      sendObjectAsResponse(output, message, consumerChannel)
    })
  }

  private def processNoInputOutputEndpointDefinition[R](e: NoInputOutputEndpointDefinition[R])(implicit
    outputManifest: Manifest[R],
    converter: MessageConverter): CloseCapable = {
    val consumerChannel = connection.newChannel()
    consumerChannel.addAutoAckConsumer(e.endpointRoute.queue, (message) => {
      sendObjectAsResponse(e.functor(), message, consumerChannel)
    })
  }

  private def processInputNoOutputEndpointDefinition[T](e: InputNoOutputEndpointDefinition[T])(implicit
    inputManifest: Manifest[T],
    converter: MessageConverter): CloseCapable = {
    val consumerChannel = connection.newChannel()

    consumerChannel.addAutoAckConsumer(e.endpointRoute.queue, (message) => {
      val input = converter.fromPayload(message.body)(inputManifest)
      e.functor(input)
    })
  }

  private def processNoInputNoOutputEndpointDefinition(e: NoInputNoOutputEndpointDefinition): CloseCapable = {
    val consumerChannel = connection.newChannel()
    consumerChannel.addAutoAckConsumer(e.endpointRoute.queue, (_) => e.functor())
  }

  private def getEndpointSignature(e: EndpointDefinition):String = {
    e match {
      case end: InputOutputEndpointDefinition[_, _] => "[" + end.inputManifest.runtimeClass + " => " + end.outputManifest.runtimeClass + "]"
      case end: NoInputOutputEndpointDefinition[_] => "[() => " + end.outputManifest.runtimeClass + "]"
      case end: InputNoOutputEndpointDefinition[_] => "[" + end.inputManifest.runtimeClass + " => Unit]"
      case end: NoInputNoOutputEndpointDefinition => "[() => ()]"
    }
  }

  private def registerConsumerHandles(handles: Seq[CloseCapable]) = _consumerHandles ++= handles


  private def processEndpointDefinition(e: EndpointDefinition)(implicit converter: MessageConverter): Seq[CloseCapable] = {

    //Internal function for easing things up
    def process(e:EndpointDefinition)(implicit converter: MessageConverter): CloseCapable = {
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

    val handles = for { _ <- 0 until e.numConsumers } yield process(e)
    _logger.info("attached " + e.numConsumers + " consumers at " + e.endpointRoute + " with functor " + getEndpointSignature(e))

    handles
  }

  override def processEndpointDefinitions(endpointDefinitions: Seq[EndpointDefinition])(implicit converter: MessageConverter): Unit = {
    endpointDefinitions.foreach(endpointDefinition => {
      ensureBindingsArePresent(endpointDefinition.endpointRoute) match {
        case Success(_) => registerConsumerHandles(processEndpointDefinition(endpointDefinition))
        case Failure(error) => _logger.error("Unable to bind " + endpointDefinition.endpointRoute, error)
      }
    })
  }


  override def close(): Unit = {
    consumerHandles.foreach(_.close())
    adminChannel.close()
    connection.close()
  }

}

object DefaultEndpointDefinitionProcessor {
  def apply(connection: ConnectionWrapper): DefaultEndpointDefinitionProcessor =
    new DefaultEndpointDefinitionProcessor(connection)
}
