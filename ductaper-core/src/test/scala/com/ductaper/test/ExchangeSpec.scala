package com.ductaper.test

import com.ductaper.core.exchange._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by zahari on 05/03/2017.
  */
class ExchangeSpec extends FlatSpec with Matchers {
  val args = Map("key" -> "val")

  "Exchange" should "get correct args when created as DirectExchange" in {
    val exch = DirectExchange("name",false,false,args)
    exch.name should be ("name")
    exch.autoDelete should be (false)
    exch.durable should be (false)
    exch.args should be (args)
    assert(exch.isInstanceOf[DirectExchange])
  }

  it should "create durable non autodelete exchange when no args are specified for DirectExchange" in {
    val exch = DirectExchange("name")
    exch.name should be ("name")
    exch.autoDelete should be (false)
    exch.durable should be (true)
    exch.args should be (Map())
    assert(exch.isInstanceOf[DirectExchange])
  }

  "Exchange" should "get correct args when created as FanoutExchange" in {
    val exch = FanoutExchange("name",false,false,args)
    exch.name should be ("name")
    exch.autoDelete should be (false)
    exch.durable should be (false)
    exch.args should be (args)
    assert(exch.isInstanceOf[FanoutExchange])
  }

  it should "create durable non autodelete exchange when no args are specified for FanoutExchange" in {
    val exch = FanoutExchange("name")
    exch.name should be ("name")
    exch.autoDelete should be (false)
    exch.durable should be (true)
    exch.args should be (Map())
    assert(exch.isInstanceOf[FanoutExchange])
  }

  "Exchange" should "get correct args when created as TopicExchange" in {
    val exch = TopicExchange("name",false,false,args)
    exch.name should be ("name")
    exch.autoDelete should be (false)
    exch.durable should be (false)
    exch.args should be (args)
    assert(exch.isInstanceOf[TopicExchange])
  }

  it should "create durable non autodelete exchange when no args are specified for TopicExchange" in {
    val exch = TopicExchange("name")
    exch.name should be ("name")
    exch.autoDelete should be (false)
    exch.durable should be (true)
    exch.args should be (Map())
    assert(exch.isInstanceOf[TopicExchange])
  }

  "Exchange" should "get correct args when created as HeadersExchange" in {
    val exch = HeadersExchange("name",false,false,args)
    exch.name should be ("name")
    exch.autoDelete should be (false)
    exch.durable should be (false)
    exch.args should be (args)
    assert(exch.isInstanceOf[HeadersExchange])
  }

  it should "create durable non autodelete exchange when no args are specified for HeadersExchange" in {
    val exch = HeadersExchange("name")
    exch.name should be ("name")
    exch.autoDelete should be (false)
    exch.durable should be (true)
    exch.args should be (Map())
    assert(exch.isInstanceOf[HeadersExchange])
  }

  it should "get a direct nameless, durable non auto delete exchange for DEFAULT_EXCHANGE" in {
    val exch = Exchange.DEFAULT_EXCHANGE
    exch.name should be ("")
    exch.autoDelete should be (false)
    exch.durable should be (true)
    exch.args should be (Map())
    assert(exch.isInstanceOf[DirectExchange])
  }


}
