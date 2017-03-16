package com.ductaper.core.message

import com.ductaper.core.message.Key.Headers
import com.rabbitmq.client.AMQP.BasicProperties

/**
  * @author Zahari Dichev <zaharidichev@gmail.com>.
  */
object Message {
  def apply(messageProperties: MessageProps, body: MessagePayload): Message = new Message(messageProperties, body)
  def apply(messageProperties: BasicProperties, body: Array[Byte]): Message = {
    val props = MessageProps(messageProperties)
    val messageBody = new MessagePayload(body)
    Message(props, messageBody)
  }
}

class Message(val messageProperties: MessageProps, val body: MessagePayload) {

  def property[T](name: BasicKey[T]): Option[T] = messageProperties.property(name)

  def headers: Option[Map[String, Any]] = messageProperties.property(Headers)

  def header(name: String): Option[Any] = headers.flatMap(x => x.get(name))

  override def toString: String = {
    val bodyToString = body.asString
    s"Message($messageProperties, body=$bodyToString)"
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Message]

  override def equals(other: Any): Boolean = other match {
    case that: Message =>
      (that canEqual this) &&
        messageProperties == that.messageProperties &&
        body == that.body
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(messageProperties, body)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
