package com.ductaper.test

import com.ductaper.core.connection.ConnectionManager
import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.route.QueueDeclare
import com.ductaper.core.thinwrappers.{ChannelThinWrapper, ConnectionThinWrapper}
import com.rabbitmq.client.impl.AMQImpl.Queue.DeclareOk
import com.rabbitmq.client.{AMQP, Consumer, ShutdownListener, ShutdownSignalException}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}

/**
 * Created by zahari on 05/03/2017.
 */
class ConnectionManagerSpec extends WordSpecLike with Matchers with MockFactory {

  //noinspection ScalaStyle
  class MockableThinWrapper extends ConnectionThinWrapper(null)
  val connectionThinWrapperMock = mock[MockableThinWrapper]

  (connectionThinWrapperMock.isOpen _) expects () returning true
  (connectionThinWrapperMock.addShutdownListener: (ShutdownListener) => Unit) expects (*)

  val connectionWrapper = ConnectionManager(connectionThinWrapperMock,(x) => ())

  "ConnectionManager" when {

    "newChannel" should {

      "call the new channel method on the underlying thin wrapper" in {
        (connectionThinWrapperMock.createChannel _) expects ()
        connectionWrapper.newChannel
      }

    }


    "close" should {

      "call the new close method on the underlying thin wrapper" in {
        (connectionThinWrapperMock.close _) expects ()
        connectionWrapper.close
      }

    }

  }

}
