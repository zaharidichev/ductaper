package com.ductaper.core.route

import java.util.UUID

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

/** Parameters to create a new queue */

trait Queue{
  def name: Option[String]
  def nameOrEmpty: String = name.getOrElse("")
}

/*  Actively declare a server-named exclusive, autodelete, non-durable queue.
  * The name of the new queue is held in the "queue" field of the {@link com.rabbitmq.client.AMQP.Queue.DeclareOk} result.
  */
case class QueueDeclare(
  override val name: Option[String] = Some(UUID.randomUUID().toString),
  durable: Boolean = false,
  exclusive: Boolean = true,
  autoDelete: Boolean = true,
  args: Map[String, AnyRef] = Map.empty
) extends Queue

case class QueuePassive(override val name:Option[String]) extends Queue
