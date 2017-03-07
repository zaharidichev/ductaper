package com.ductaper.core.connection

import com.ductaper.core.channel.{ChannelManager, ChannelWrapper}
import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.thinwrappers.{ChannelThinWrapper, ConnectionThinWrapper}
import com.rabbitmq.client.{Channel, ShutdownSignalException}

import scala.concurrent.blocking

/**
 * Created by zahari on 06/02/2017.
 */
class ConnectionManager(conn: ConnectionThinWrapper, eventListener: SystemEvent ⇒ Unit) extends ConnectionWrapper {

  if (conn.isOpen) {
    logger.info("Opened connection " + conn)
    conn.addShutdownListener((cause: ShutdownSignalException) => logger.info("Shutdown completed " + cause.getMessage))
  }

  override def newChannel(): ChannelWrapper = new ChannelManager(ChannelThinWrapper(createChannel(conn)), eventListener)

  override def close(): Unit = {
    logger.info("Closing connection: " + conn)
    blocking {
      conn.close()
    }
  }

  private def createChannel(conn: ConnectionThinWrapper): Channel = blocking {
    conn.createChannel
  }

}

object ConnectionManager {
  def apply(conn: ConnectionThinWrapper, eventListener: SystemEvent ⇒ Unit): ConnectionManager = new ConnectionManager(conn, eventListener)
}
