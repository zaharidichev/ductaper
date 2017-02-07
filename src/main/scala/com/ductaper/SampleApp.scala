package com.ductaper

import java.util.concurrent.TimeUnit

import com.ductaper.core.message.DeliveryMode.Persistent
import com.ductaper.core.message.Key.{ContentType, CorrelationId, ReplyTo}
import com.ductaper.core.connection.ReconnectionStrategy.SimpleReconnectionStrategy
import com.ductaper.core.connection.{ConnectionManager, ConnectionWrapper}
import com.ductaper.core.container.DuctaperRouteResult
import com.ductaper.core.message.{Key, Message, MessagePayload, MessageProps}
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}
import com.ductaper.core.serialization.JsonMessageConvertor
import org.slf4j.LoggerFactory
import com.ductaper.core.serialization.JsonMessageConvertor._

import scala.concurrent.duration.FiniteDuration

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
case class Person(name: String, profession: String)

object SampleApp extends App {

  /*  val logger = LoggerFactory.getLogger(classOf[App])
  val connection = ConnectionWrapper.builder("amqp://guest:guest@localhost:5672",ConnectionManager.apply).connectionTimeout(5000).reconnectionStrategy(SimpleReconnectionStrategy(new FiniteDuration(10, TimeUnit.SECONDS))).build()
  val chan = connection.newChannel()
  val ex = chan.declareExchangePassive("my-exchange");

  val message = Message(MessageProps(CorrelationId -> "correlation-id",
    ReplyTo -> "replyQueue",
    ContentType -> "application/json",
    Key.DeliveryMode -> Persistent),
    MessagePayload("This is a sample message")
  )




  chan.send(BrokerRoutingData(ex, RoutingKey("my-key")), message)
  logger.info("Sent message: " + message)
  //chan.close()
  connection.close()*/

  val toSerialize = Person("zahari", "developer")
  val payload = MessagePayload(toSerialize)

  val toDeserialize = MessagePayload("{\"name\":\"zahari\",\"profession\":\"developer\"}")

  val deserialzied = deserializeFromPayload[Person](toDeserialize)

  println(payload);

}
