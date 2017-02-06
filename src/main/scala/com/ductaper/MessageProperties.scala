package com.ductaper

import java.util.Date

import com.ductaper.Key._
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.AMQP.BasicProperties.Builder

import scala.collection.JavaConverters._
import scala.language.implicitConversions

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */



  trait BasicKey {
  }

  object Key {
    case object ContentType extends BasicKey
    case object ContentEncoding extends BasicKey
    case object Type extends BasicKey
    case object Timestamp extends BasicKey
    case object MessageId extends BasicKey
    case object ReplyTo extends BasicKey
    case object DeliveryMode extends BasicKey
    case object UserId extends BasicKey
    case object Expiration extends BasicKey
    case object Priority extends BasicKey
    case object Headers extends BasicKey
    case object CorrelationId extends BasicKey
    case object AppId extends BasicKey
  }

class MessageProps(private val properties:Map[BasicKey,Any]) {

  def ++(messageProperties: MessageProps): MessageProps =
    new MessageProps(properties ++ messageProperties.properties)

  def ++(elems: (BasicKey, Any)*): MessageProps =
    new MessageProps(properties ++ elems)

  def +(elems: (BasicKey, Any)): MessageProps =
    new MessageProps(properties + elems)

  private implicit def conv1(x: Any):String = x.asInstanceOf[String]
  private implicit def conv3(x: Any):java.util.Map[java.lang.String,java.lang.Object] = mapAsJavaMap(x.asInstanceOf[Map[String,Object]])
  private implicit def conv6(x: Any):Integer = new Integer(x.asInstanceOf[Int])


  def get(name:BasicKey):Option[Any] = properties.get(name)

  def getOrNull(name:BasicKey):Any = properties.get(name).getOrElse(null)


  def toJavaBasicProps:BasicProperties ={
    val nativePropesBuilder = new Builder;
    properties.get(ContentType).foreach(x => nativePropesBuilder.contentType(x))
    properties.get(ContentEncoding).foreach(x => nativePropesBuilder.contentEncoding(x))
    properties.get(Type).foreach(x => nativePropesBuilder.`type`(x.toString))
    properties.get(Timestamp).foreach(x => nativePropesBuilder.timestamp(x.asInstanceOf[Date]))
    properties.get(MessageId).foreach(x => nativePropesBuilder.messageId(x.toString))
    properties.get(ReplyTo).foreach(x => nativePropesBuilder.replyTo(x.toString))
    properties.get(Key.DeliveryMode).foreach(x => nativePropesBuilder.deliveryMode(new Integer(x.asInstanceOf[DeliveryMode].mode)))
    properties.get(UserId).foreach(x => nativePropesBuilder.userId(x.toString))
    properties.get(Expiration).foreach(x => nativePropesBuilder.expiration(x.toString))
    properties.get(Priority).foreach(x => nativePropesBuilder.priority(x))
    properties.get(Headers).foreach(x => nativePropesBuilder.headers(x))
    properties.get(CorrelationId).foreach(x => nativePropesBuilder.correlationId(x.toString))
    properties.get(AppId).foreach(x => nativePropesBuilder.appId(x.toString))
    nativePropesBuilder.build();
  }

  override def equals(other: Any): Boolean = other match {
    case that: MessageProps ⇒ properties == that.properties
    case _ ⇒ false
  }

  override def hashCode(): Int = properties.hashCode()

  override def toString = s"MessageProps($properties)"
}


object MessageProps {

  def apply(properties: Map[BasicKey, Any]): MessageProps = new MessageProps(properties)

  def apply(prop: (BasicKey, Any)): MessageProps = new MessageProps((Map.newBuilder += (prop)).result())


  def apply(properties: (BasicKey, Any)*): MessageProps = {
    val propertiesMap: Map[BasicKey, Any] = (Map.newBuilder ++= properties.filterNot(_._2 == null)).result()
    new MessageProps(propertiesMap)
  }

  def apply(bp: AMQP.BasicProperties): MessageProps =  MessageProps(
    ContentType -> bp.getContentType,
    ContentEncoding -> bp.getContentEncoding,
    Type -> bp.getType,
    Timestamp -> bp.getTimestamp,
    MessageId -> bp.getMessageId,
    ReplyTo -> bp.getReplyTo,
    Key.DeliveryMode -> bp.getDeliveryMode,
    UserId -> bp.getUserId,
    Expiration -> bp.getExpiration,
    Priority -> bp.getPriority,
    Headers -> bp.getHeaders,
    CorrelationId -> bp.getCorrelationId,
    AppId -> bp.getAppId
  )

}

sealed abstract class DeliveryMode(val mode: Int)


object DeliveryMode {

  def apply(value: Int) = value match {
    case 1 ⇒ NotPersistent
    case 2 ⇒ Persistent
  }

  case object NotPersistent extends DeliveryMode(1)

  case object Persistent extends DeliveryMode(2)

}



