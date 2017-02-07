package com.ductaper.core.events

import com.ductaper.core.events.Event.SystemEvent

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
object Event {

  trait SystemEvent

  trait ConnectionEvent extends SystemEvent

  object ConnectionEvent {
    case object ConnectionShutdown extends ConnectionEvent
    case object ConnectionOpen extends ConnectionEvent
  }

  trait ChannelEvent extends SystemEvent
  object ChannelEvent {
    case object ConnectionShutdown extends ConnectionEvent
    case object ConnectionOpen extends ConnectionEvent
  }
}

object Listener {
  def noOpEventListener: SystemEvent ⇒ Unit = (x: SystemEvent) ⇒ {}
  def printingEventListener: SystemEvent ⇒ Unit = (x: SystemEvent) ⇒ println(x)
}