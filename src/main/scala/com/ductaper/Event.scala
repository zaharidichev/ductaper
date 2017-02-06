package com.ductaper

import com.ductaper.Event.SystemEvent

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
}


object Listener {
  def noOpEventListener: SystemEvent => Unit = (x:SystemEvent) => {}
  def printingEventListener: SystemEvent => Unit = (x:SystemEvent) => println(x)
}