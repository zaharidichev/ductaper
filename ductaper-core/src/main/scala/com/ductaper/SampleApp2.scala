package com.ductaper

import java.util.concurrent.TimeUnit

import com.ductaper.core.connection.ReconnectionStrategy.SimpleReconnectionStrategy
import com.ductaper.core.connection.{ConnectionManager, ConnectionWrapper}
import com.ductaper.core.message.Message
import com.ductaper.core.route.{RoutingKey}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.FiniteDuration;

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
object SampleApp2 extends App {

/*  val logger = LoggerFactory.getLogger(classOf[App])
  val connection = ConnectionWrapper.builder("amqp://guest:guest@localhost:5672", ConnectionManager.apply).connectionTimeout(5000).reconnectionStrategy(SimpleReconnectionStrategy(new FiniteDuration(10, TimeUnit.SECONDS))).build()
  val chan = connection.newChannel()
  val ex = chan.declareExchangePassive("my-exchange");
  val q = chan.declareQueue(QueueDeclare(Some("myQueue")))
  chan.queueBind(q, ex, RoutingKey("my-key"))
  def consumator = (x: Message) ⇒ {
    println(x)
  }

  chan.addAutoAckConsumer(q, consumator)*/

}
