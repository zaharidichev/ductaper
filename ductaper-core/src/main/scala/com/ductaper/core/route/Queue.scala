package com.ductaper.core.route

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

/** Parameters to create a new queue */

trait Queue{
  def name: Option[String]
  def nameOrEmpty: String = name.getOrElse("")
}

case class QueueDeclare(
  override val name: Option[String],
  durable: Boolean = true,
  exclusive: Boolean = false,
  autoDelete: Boolean = false,
  args: Map[String, AnyRef] = Map.empty
) extends Queue

case class QueuePassive(override val name:Option[String]) extends Queue
