package com.ductaper.core.connection

import com.ductaper.core.CloseCapable
import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.channel.ChannelWrapper
import com.ductaper.core.events.Listener
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory

import scala.concurrent._

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

trait ConnectionWrapper extends CloseCapable {
  val logger = LoggerFactory.getLogger(classOf[ConnectionWrapper])
  def newChannel(): ChannelWrapper
}

trait ConnectionWrapperBuilder {

  def connectionTimeout(i: Int): ConnectionWrapperBuilder
  def eventListener(i: SystemEvent ⇒ Unit): ConnectionWrapperBuilder
  def reconnectionStrategy(i: ReconnectionStrategy): ConnectionWrapperBuilder
  def build(): ConnectionWrapper
}

object ConnectionWrapper {

  def builder(
    url: String,
    supplier: (Connection, SystemEvent ⇒ Unit) ⇒ ConnectionWrapper
  ): ConnectionWrapperBuilder = {
    Builder(url, supplier)
  }

  private case class Builder(
    _url: String,
      _connectionWrapperSupplier: (Connection, SystemEvent ⇒ Unit) ⇒ ConnectionWrapper,
      _connectionTimeout: Option[Int] = None,
      _eventListener: SystemEvent ⇒ Unit = Listener.printingEventListener,
      _reconnectionStrategy: ReconnectionStrategy = ReconnectionStrategy.NoReconnect
  ) extends ConnectionWrapperBuilder {

    private def buildNativeConnectionFactory(): ConnectionFactory = {
      val nativeConnectionFactory = new ConnectionFactory()
      nativeConnectionFactory.setUri(_url)
      _connectionTimeout.foreach(nativeConnectionFactory.setConnectionTimeout)

      _reconnectionStrategy match {
        case ReconnectionStrategy.SimpleReconnectionStrategy(interval) ⇒
          nativeConnectionFactory.setAutomaticRecoveryEnabled(true)
          nativeConnectionFactory.setNetworkRecoveryInterval(interval.toMillis)
        case _ ⇒ {}
      }

      nativeConnectionFactory
    }

    override def connectionTimeout(i: Int): ConnectionWrapperBuilder = copy(_connectionTimeout = Some(i))
    override def eventListener(i: SystemEvent ⇒ Unit): ConnectionWrapperBuilder = copy(_eventListener = i)
    override def reconnectionStrategy(i: ReconnectionStrategy): ConnectionWrapperBuilder = copy(_reconnectionStrategy = i)

    override def build(): ConnectionWrapper = {
      val conn = buildNativeConnectionFactory().newConnection()
      _connectionWrapperSupplier(conn, _eventListener)
    }
  }
}

