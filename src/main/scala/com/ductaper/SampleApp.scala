package com.ductaper

import java.util.concurrent.TimeUnit

import com.ductaper.DeliveryMode.Persistent
import com.ductaper.Key.{ContentType, CorrelationId, ReplyTo}
import com.ductaper.ReconnectionStrategy.SimpleReconnectionStrategy
import org.slf4j.LoggerFactory

import scala.concurrent.duration.FiniteDuration


/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
object SampleApp extends App {

	val logger = LoggerFactory.getLogger(classOf[App])
	val connection = ConnectionWrapper.builder("amqp://guest:guest@localhost:5672").connectionTimeout(5000).reconnectionStrategy(SimpleReconnectionStrategy(new FiniteDuration(10,TimeUnit.SECONDS))).build()
	val chan = connection.newChannel()
	val ex = chan.declareExchangePassive("my-exchange");

	val message = Message(MessageProps(CorrelationId -> "correlation-id",
																		 ReplyTo -> "replyQueue",
																		 ContentType -> "application/json",
																		 Key.DeliveryMode -> Persistent),
																		 MessagePayload("This is a sample message")
	)

	chan.send(BrokerRoutingData(ex,RoutingKey("my-key")),message)
	logger.info("Sent message: " + message)
	//chan.close()
	connection.close()
}
