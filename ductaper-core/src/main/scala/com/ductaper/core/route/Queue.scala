package com.ductaper.core.route

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

/** Parameters to create a new queue */
case class Queue(
  name: Option[String],
  durable: Boolean = true,
  exclusive: Boolean = false,
  autoDelete: Boolean = false,
  args: Map[String, AnyRef] = Map.empty
)
