package com.ductaper.core.exchange

import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */


trait Exchange {
  val name: String
  val durable: Boolean
  val autoDelete: Boolean
  val args: Map[String, AnyRef]
  def exchangeType:ExchangeType
}

case class DirectExchange(
  override val name: String,
  override val durable: Boolean = true,
  override val autoDelete: Boolean = false,
  override val args: Map[String, AnyRef] = Map.empty
) extends Exchange {
  override def exchangeType: ExchangeType = ExchangeType.Direct
}

case class FanoutExchange(
  override val name: String,
  override val durable: Boolean = true,
  override val autoDelete: Boolean = false,
  override val args: Map[String, AnyRef] = Map.empty
) extends Exchange {
  override def exchangeType: ExchangeType = ExchangeType.Fanout
}

case class TopicExchange(
  override val name: String,
  override val durable: Boolean = true,
  override val autoDelete: Boolean = false,
  override val args: Map[String, AnyRef] = Map.empty
) extends Exchange {
  override def exchangeType: ExchangeType = ExchangeType.Topic
}

case class HeadersExchange(
  override val name: String,
  override val durable: Boolean = true,
  override val autoDelete: Boolean = false,
  override val args: Map[String, AnyRef] = Map.empty
) extends Exchange {
  override def exchangeType: ExchangeType = ExchangeType.Headers
}

object Exchange {
  val DEFAULT_EXCHANGE = DirectExchange("")
}

