
package com.ductaper.test

import com.ductaper.core.channel.{ChannelManager, ChannelWrapper}
import com.ductaper.core.events.Event.SystemEvent
import com.ductaper.core.message.Key.{ContentType, Headers}
import com.ductaper.core.message.{BasicKey, Message, MessagePayload, MessageProps}
import com.rabbitmq.client.Channel
import com.rabbitmq.client.impl.AMQImpl.Queue.DeclareOk
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpecLike}

/**
 * Created by zahari on 07/02/2017.
 */
class MessagePayloadSpec extends WordSpecLike with Matchers with MockFactory {

  class MockableMessagePayload extends MessagePayload(Array.emptyByteArray)

  "MessagePayload" when {
    val messagePropsMock = mock[MessageProps]
    val messagePayloadMock = mock[MockableMessagePayload]

    "property" should {
      "call get on the props object" in {
        val message = Message(messagePropsMock, messagePayloadMock)
        (messagePropsMock.property[String] _) expects ContentType
        message.property(ContentType)
      }

    }

    "headers" should {
      "call get for headers object" in {
        val message = Message(messagePropsMock, messagePayloadMock)
        (messagePropsMock.property[Map[String,AnyRef]] _) expects Headers
        message.headers
      }
    }

  }

}


