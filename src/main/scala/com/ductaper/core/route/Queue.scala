package com.ductaper.core.route

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */


/** Parameters to create a new queue */
case class Queue(
  name: Option[String],
  durable: Boolean = false,
  exclusive: Boolean = false,
  autoDelete: Boolean = true,
  args: Map[String, AnyRef] = Map.empty
)

