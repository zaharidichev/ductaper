package com.ductaper.core.connection

import com.ductaper.core.channel.{ChannelManager, ChannelWrapper}
import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.thinwrappers.ChannelThinWrapper
import com.rabbitmq.client.{Channel, Connection, ShutdownListener, ShutdownSignalException}

import scala.concurrent.blocking

/**
 * Created by zahari on 06/02/2017.
 */
class ConnectionManager(conn: Connection, eventListener: SystemEvent ⇒ Unit) extends ConnectionWrapper {

  if (conn.isOpen) {
    logger.info("Opened connection " + conn)
    conn.addShutdownListener(new ShutdownListener {
      override def shutdownCompleted(cause: ShutdownSignalException): Unit = logger.info("Shutdown completed " + cause.getMessage)
    })
  }

  override def newChannel(): ChannelWrapper = new ChannelManager(ChannelThinWrapper(createChannel(conn)), eventListener)

  override def close(): Unit = {
    logger.info("Closing connection: " + conn)
    blocking {
      conn.close()
    }
  }

  private def createChannel(conn: Connection): Channel = blocking {
    conn.createChannel()
  }

}

object ConnectionManager {
  def apply(conn: Connection, eventListener: SystemEvent ⇒ Unit): ConnectionManager = new ConnectionManager(conn, eventListener)
}
