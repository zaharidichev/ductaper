package com.ductaper

import com.ductaper.Event.SystemEvent
import com.rabbitmq.client.{Channel, _}
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
  def eventListener(i: SystemEvent => Unit): ConnectionWrapperBuilder
  def reconnectionStrategy(i: ReconnectionStrategy): ConnectionWrapperBuilder
  def build(): ConnectionWrapper
}


class ConnectionManager(conn: Connection,eventListener: SystemEvent => Unit) extends ConnectionWrapper {

  if (conn.isOpen) {
    logger.info("Opened connection " + conn)
    conn.addShutdownListener(new ShutdownListener {
      override def shutdownCompleted(cause: ShutdownSignalException): Unit = logger.info("Shutdown completed " + cause.getMessage)
    })
  }

  override def newChannel(): ChannelWrapper = {
    val c = createChannel(conn);
    new ChannelManager(c,eventListener)
  }

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


object ConnectionWrapper {

  def builder(url: String): ConnectionWrapperBuilder = new Builder(url)

  private case class Builder(_url: String,
                             _connectionTimeout: Option[Int] = None,
                             _eventListener: SystemEvent => Unit = Listener.printingEventListener,
                             _reconnectionStrategy: ReconnectionStrategy = ReconnectionStrategy.NoReconnect
                             ) extends ConnectionWrapperBuilder {


    private def buildNativeConnectionFactory(): ConnectionFactory = {
      val nativeConnectionFactory = new ConnectionFactory();
      nativeConnectionFactory.setUri(_url)
      _connectionTimeout.foreach(nativeConnectionFactory.setConnectionTimeout(_))

      _reconnectionStrategy match {
        case ReconnectionStrategy.SimpleReconnectionStrategy(interval) =>
          nativeConnectionFactory.setAutomaticRecoveryEnabled(true)
          nativeConnectionFactory.setNetworkRecoveryInterval(interval.toMillis)
        case _ => {}
      }


      nativeConnectionFactory
    }

    override def connectionTimeout(i: Int): ConnectionWrapperBuilder = copy(_connectionTimeout = Some(i))
    override def eventListener(i: SystemEvent => Unit): ConnectionWrapperBuilder = copy(_eventListener = i)
    override def reconnectionStrategy(i: ReconnectionStrategy): ConnectionWrapperBuilder = copy(_reconnectionStrategy = i)


    override def build(): ConnectionWrapper = {
      val conn = buildNativeConnectionFactory().newConnection()
      new ConnectionManager(conn,_eventListener)
    }
  }
}

