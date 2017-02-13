package com.ductaper.core.container

import com.ductaper.core.CloseCapable
import com.ductaper.core.channel.{ChannelWrapper, ConsumerHandle}
import com.ductaper.core.connection.ConnectionWrapper
import com.ductaper.core.message.Message
import com.ductaper.core.route.Queue

import scala.collection.immutable.HashSet
import scala.collection.mutable

/**
 * Created by zahari on 07/02/2017.
 */
class MessageProcessingContainer(connection: ConnectionWrapper) extends CloseCapable {

  private val _adminChannel: ChannelWrapper = connection.newChannel()
  private val _consumerHandles = scala.collection.mutable.HashSet[CloseCapable]()

  def adminChannel: ChannelWrapper = _adminChannel
  def consumerHandles: mutable.HashSet[CloseCapable] = _consumerHandles

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
