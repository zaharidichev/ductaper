package com.ductaper

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

trait Queue

/** Parameters to create a new queue */
case class QueueDeclare(name: Option[String],
                        durable: Boolean = false,
                        exclusive: Boolean = false,
                        autoDelete: Boolean = true,
                        args: Map[String, AnyRef] = Map.empty) extends Queue


case class QueuePassive(name: String) extends Queue

case class QueueDeclared(name: String) extends Queue


