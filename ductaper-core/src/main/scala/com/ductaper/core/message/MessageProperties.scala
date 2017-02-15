package com.ductaper.core.message

import java.util.Date

import com.ductaper.core.message.Key._
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.AMQP.BasicProperties.Builder

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.collection.JavaConverters._

import com.ductaper.core.exchange.Exchange._

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

case class MessageProps(
  contentType: Option[String] = None,
  contentEncoding: Option[String] = None,
  messageType: Option[String] = None,
  timestamp: Option[Date] = None,
  messageId: Option[String] = None,
  replyTo: Option[BrokerRoutingData] = None,
  deliveryMode: Option[DeliveryMode] = None,
  userId: Option[String] = None,
  expiration: Option[Int] = None,
  priority: Option[Int] = None,
  headers: Option[Map[String, AnyRef]] = None,
  correlationId: Option[String] = None,
  appId: Option[String] = None
) {
  def contentType(i: String): MessageProps = copy(contentType = Some(i))
  def contentEncoding(i: String): MessageProps = copy(contentEncoding = Some(i))
  def messageType(i: String): MessageProps = copy(messageType = Some(i))
  def timestamp(i: Date): MessageProps = copy(timestamp = Some(i))
  def messageId(i: String): MessageProps = copy(messageId = Some(i))
  def replyTo(i: BrokerRoutingData): MessageProps = copy(replyTo = Some(i))
  def deliveryMode(i: DeliveryMode): MessageProps = copy(deliveryMode = Some(i))
  def userId(i: String): MessageProps = copy(userId = Some(i))
  def expiration(i: Int): MessageProps = copy(expiration = Some(i))
  def priority(i: Int): MessageProps = copy(priority = Some(i))
  def headers(i: Map[String, AnyRef]): MessageProps = copy(headers = Some(i))
  def correlationId(i: String): MessageProps = copy(correlationId = Some(i))
  def appId(i: String): MessageProps = copy(appId = Some(i))

  def property[T](basicKey: BasicKey[T]): Option[T] = basicKey.getterFunc(this)

  def toJavaBasicProps: BasicProperties = {
    val nativePropertiesBuilder = new Builder;
    contentType.foreach(x ⇒ nativePropertiesBuilder.contentType(x))
    contentEncoding.foreach(x ⇒ nativePropertiesBuilder.contentEncoding(x))
    messageType.foreach(x ⇒ nativePropertiesBuilder.`type`(x.toString))
    timestamp.foreach(x ⇒ nativePropertiesBuilder.timestamp(x))
    messageId.foreach(x ⇒ nativePropertiesBuilder.messageId(x.toString))
    replyTo.foreach(x ⇒ nativePropertiesBuilder.replyTo(x.toString))
    deliveryMode.foreach(x ⇒ nativePropertiesBuilder.deliveryMode(x.mode))
    userId.foreach(x ⇒ nativePropertiesBuilder.userId(x.toString))
    expiration.foreach(x ⇒ nativePropertiesBuilder.expiration(x.toString))
    priority.foreach(x ⇒ nativePropertiesBuilder.priority(x))
    headers.foreach(x ⇒ nativePropertiesBuilder.headers(mapAsJavaMap(x)))
    correlationId.foreach(x ⇒ nativePropertiesBuilder.correlationId(x.toString))
    appId.foreach(x ⇒ nativePropertiesBuilder.appId(x.toString))
    nativePropertiesBuilder.build();
  }

}

sealed class BasicKey[T](val getterFunc: MessageProps => Option[T]) {
  //def --> (value:T) = BasicKeyValue(this,value)
}

case class BasicKeyValue[T](key: BasicKey[T], value: T)

object Key {
  case object ContentType extends BasicKey[String](x => x.contentType)
  case object ContentEncoding extends BasicKey[String](x => x.contentEncoding)
  case object Type extends BasicKey[String](x => x.messageType)
  case object Timestamp extends BasicKey[Date](x => x.timestamp)
  case object MessageId extends BasicKey[String](x => x.messageId)
  case object ReplyTo extends BasicKey[BrokerRoutingData](x => x.replyTo)
  case object DeliveryMode extends BasicKey[DeliveryMode](x => x.deliveryMode)
  case object UserId extends BasicKey[String](x => x.userId)
  case object Expiration extends BasicKey[Int](x => x.expiration)
  case object Priority extends BasicKey[Int](x => x.priority)
  case object Headers extends BasicKey[Map[String, AnyRef]](x => x.headers)
  case object CorrelationId extends BasicKey[String](x => x.correlationId)
  case object AppId extends BasicKey[String](x => x.appId)
}

object MessageProps {
  def apply: MessageProps = new MessageProps()
}

sealed abstract class DeliveryMode(val mode: Int)
object DeliveryMode {

  def apply(value: Int): DeliveryMode = value match {
    case 1 ⇒ NotPersistent
    case 2 ⇒ Persistent
  }

  case object NotPersistent extends DeliveryMode(1)

  case object Persistent extends DeliveryMode(2)

}

