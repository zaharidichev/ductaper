package com.ductaper.core.thinwrappers

import java.io.IOException

import com.rabbitmq.client.{Channel, Connection, ShutdownListener}

/**
  * Created by zahari on 05/03/2017.
  */
class ConnectionThinWrapper(private val nativeConnection:Connection) {

  @throws[IOException]
  def close():Unit = nativeConnection.close()

  def addShutdownListener(listener: ShutdownListener):Unit = nativeConnection.addShutdownListener(listener)

  @throws[IOException]
  def createChannel: Channel = nativeConnection.createChannel()

  def isOpen: Boolean = nativeConnection.isOpen
}

object ConnectionThinWrapper {
  def apply(c: Connection):ConnectionThinWrapper = new ConnectionThinWrapper(c)
}
