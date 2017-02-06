package com.ductaper

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

abstract class ExchangeType(val name: String)

object ExchangeType {
  case object Direct extends ExchangeType("direct")
  case object Topic extends ExchangeType("topic")
  case object Fanout extends ExchangeType("fanout")
  case object Headers extends ExchangeType("headers")
}


