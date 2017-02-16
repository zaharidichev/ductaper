package com.ductaper.core.connection

import com.ductaper.core.channel.ChannelWrapper
import com.ductaper.core.configuration.{ConnectionConfiguration, FileBasedConnectionConfiguration}
import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.misc.CloseCapable
import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory

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

  def getConnection(config: ConnectionConfiguration =  new FileBasedConnectionConfiguration()): ConnectionWrapper = {
    val nativeConnection = buildNativeConnectionFactory(config).newConnection()
    new ConnectionManager(nativeConnection, x => ())
    }

  private def buildNativeConnectionFactory(config: ConnectionConfiguration): ConnectionFactory = {
    val nativeConnectionFactory = new ConnectionFactory()
    nativeConnectionFactory.setUri(config.uri)
    nativeConnectionFactory.setUsername(config.userName)
    nativeConnectionFactory.setPassword(config.password)
    nativeConnectionFactory
  }
}

