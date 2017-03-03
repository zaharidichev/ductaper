package com.ductaper.core.thinwrappers

import java.io.IOException

import com.rabbitmq.client.AMQP.{Exchange, Queue}
import com.rabbitmq.client.{AMQP, Consumer}
import com.rabbitmq.client._
import scala.collection.JavaConverters._



/**
  * Created by zahari on 03/03/2017.
  */
class ChannelThinWrapper(private val c: Channel) {

  @throws[IOException]
  def queueDeclare(queue: String, durable: Boolean, exclusive: Boolean, autoDelete: Boolean, arguments: Map[String, AnyRef]): Queue.DeclareOk = {
    c.queueDeclare(queue,durable,exclusive,autoDelete,mapAsJavaMap(arguments))
  }

  @throws[IOException]
  def queueDeclare(): Queue.DeclareOk  = c.queueDeclare

  @throws[IOException]
  def exchangeDeclare(exchange: String, exchangeType: String, durable: Boolean, autoDelete: Boolean, arguments: Map[String, AnyRef]): Exchange.DeclareOk = {
    c.exchangeDeclare(exchange,exchangeType,durable,autoDelete,mapAsJavaMap(arguments))
  }

  @throws[IOException]
  def basicConsume(queue: String, autoAck: Boolean, callback: Consumer): String = {
    c.basicConsume(queue,autoAck,callback)
  }

  @throws[IOException]
  def queueBind(queue: String, exchange: String, routingKey: String): Queue.BindOk = {
    c.queueBind(queue,exchange,routingKey)
  }

  @throws[IOException]
  def basicPublish(exchange: String, routingKey: String, props: AMQP.BasicProperties, body: Array[Byte]):Unit = {
    c.basicPublish(exchange,routingKey,props,body)
  }

  @throws[IOException]
  def close():Unit = c.close()

  @throws[IOException]
  def basicCancel(consumerTag: String):Unit = c.basicCancel(consumerTag)

  def native:Channel = c
}

object ChannelThinWrapper {
  def apply(c: Channel) = new ChannelThinWrapper(c)
}
