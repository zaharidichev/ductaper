package com.ductaper.core.message

import java.util.Date

import com.ductaper.core.message.Key._
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.AMQP.BasicProperties.Builder

import scala.collection.JavaConverters._
import scala.language.implicitConversions

import com.ductaper.core.exchange.Exchange._

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */


case class MessageProps(contentType:Option[String] = None,
             contentEncoding:Option[String] = None,
             messageType:Option[String] = None,
             timestamp:Option[Date] = None,
             messageId:Option[String] = None,
             replyTo:Option[BrokerRoutingData] = None,
             deliveryMode:Option[DeliveryMode] = None,
             userId:Option[String] = None,
             expiration:Option[Int] = None,
             priority:Option[Int] = None,
             headers:Option[Map[String,AnyRef]] = None,
             correlationId:Option[String] = None,
             appId:Option[String] = None)
{

  def contentType(i:String):MessageProps = copy(contentType = Some(i))
  def contentEncoding(i:String):MessageProps = copy(contentEncoding = Some(i))
  def messageType(i:String):MessageProps = copy(messageType = Some(i))
  def timestamp(i:Date):MessageProps = copy(timestamp=Some(i))
  def messageId(i:String):MessageProps = copy(messageId = Some(i))
  def replyTo(i:BrokerRoutingData):MessageProps = copy(replyTo = Some(i))
  def deliveryMode(i:DeliveryMode):MessageProps = copy(deliveryMode = Some(i))
  def userId(i:String):MessageProps = copy(userId = Some(i))
  def expiration(i:Int):MessageProps = copy(expiration = Some(i))
  def priority(i:Int):MessageProps = copy(priority = Some(i))
  def headers(i:Map[String,AnyRef]):MessageProps = copy(headers = Some(i))
  def correlationId(i:String):MessageProps = copy(correlationId = Some(i))
  def appId(i:String):MessageProps = copy(appId = Some(i))

  def property[T](basicKey: BasicKey[T]):Option[T] = basicKey.getterFunc(this)


  def toJavaBasicProps: BasicProperties = {
    val nativePropesBuilder = new Builder;
    contentType.foreach(x ⇒ nativePropesBuilder.contentType(x))
    contentEncoding.foreach(x ⇒ nativePropesBuilder.contentEncoding(x))
    messageType.foreach(x ⇒ nativePropesBuilder.`type`(x.toString))
    timestamp.foreach(x ⇒ nativePropesBuilder.timestamp(x))
    messageId.foreach(x ⇒ nativePropesBuilder.messageId(x.toString))
    replyTo.foreach(x ⇒ nativePropesBuilder.replyTo(x.toString))
    deliveryMode.foreach(x ⇒ nativePropesBuilder.deliveryMode(x.mode))
    userId.foreach(x ⇒ nativePropesBuilder.userId(x.toString))
    expiration.foreach(x ⇒ nativePropesBuilder.expiration(x.toString))
    priority.foreach(x ⇒ nativePropesBuilder.priority(x))
    headers.foreach(x ⇒ nativePropesBuilder.headers(mapAsJavaMap(x)))
    correlationId.foreach(x ⇒ nativePropesBuilder.correlationId(x.toString))
    appId.foreach(x ⇒ nativePropesBuilder.appId(x.toString))
    nativePropesBuilder.build();
  }

}



class BasicKey[T](val builderFunc: (T,MessageProps) => MessageProps,val getterFunc: MessageProps => Option[T]) {
  def --> (value:T) = BasicKeyValue(this,value)
}

case class BasicKeyValue[T](key: BasicKey[T],value:T)

object Key {
  case object ContentType extends BasicKey[String]( (value,mProps) => mProps.contentType(value), x => x.contentType)
  case object ContentEncoding extends BasicKey[String]( (value,mProps) => mProps.contentEncoding(value), x => x.contentEncoding)
  case object Type extends BasicKey[String]( (value,mProps) => mProps.messageType(value),x => x.messageType)
  case object Timestamp extends BasicKey[Date]( (value,mProps) => mProps.timestamp(value),x => x.timestamp)
  case object MessageId extends BasicKey[String]( (value,mProps) => mProps.messageId(value), x => x.messageId)
  case object ReplyTo extends BasicKey[BrokerRoutingData]( (value,mProps) => mProps.replyTo(value), x=> x.replyTo)
  case object DeliveryMode extends BasicKey[DeliveryMode]( (value,mProps) => mProps.deliveryMode(value),x => x.deliveryMode)
  case object UserId extends BasicKey[String]( (value,mProps) => mProps.userId(value), x => x.userId)
  case object Expiration extends BasicKey[Int]( (value,mProps) => mProps.expiration(value), x=> x.expiration)
  case object Priority extends BasicKey[Int]( (value,mProps) => mProps.priority(value), x=> x.priority)
  case object Headers extends BasicKey[Map[String,AnyRef]]( (value,mProps) => mProps.headers(value), x=> x.headers)
  case object CorrelationId extends BasicKey[String]( (value,mProps) => mProps.correlationId(value), x => x.correlationId)
  case object AppId extends BasicKey[String]( (value,mProps) => mProps.appId(value), x => x.appId)
}


import scala.collection.JavaConverters._


object MessageProps {
  def apply: MessageProps = new MessageProps()

}



sealed abstract class DeliveryMode(val mode: Int)

object DeliveryMode {

  def apply(value: Int):DeliveryMode = value match {
    case 1 ⇒ NotPersistent
    case 2 ⇒ Persistent
  }

  case object NotPersistent extends DeliveryMode(1)

  case object Persistent extends DeliveryMode(2)

}

