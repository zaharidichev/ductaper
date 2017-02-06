package com.ductaper

import scala.concurrent.duration.FiniteDuration

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
sealed trait ReconnectionStrategy

/**
 * Strategies for reconnection, currently only fixed delay or no reconnection are available
 */
object ReconnectionStrategy {
  val default = NoReconnect

  object NoReconnect extends ReconnectionStrategy

  /**
   * Uses the underlying java client to attempt network recovery at a fixed interval
   *
   * NOTE: The java client only supports recovery of an entire connection when it is lost due to a network failure,
   * channels and consumers can also fail (due to things like using an exchange that doesn't exist on the broker) -
   * these will not be recovered.
   *
   * @param networkRecoveryInterval duration between reconnection attempts, minimum resolution in millis
   */
  case class SimpleReconnectionStrategy(networkRecoveryInterval: FiniteDuration) extends ReconnectionStrategy

}