package com.ductaper.test

import com.ductaper.core.route.{QueueDeclare, QueuePassive}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by zahari on 05/03/2017.
  */
class QueueSpec  extends FlatSpec with Matchers {
  val args = Map("key" -> "val")

  "Queue" should "get correct options when created as QueueDeclare" in {
    val declare = QueueDeclare(Some("name"),false,false,false,args)
    declare.name should be (Some("name"))
    declare.autoDelete should be (false)
    declare.durable should be (false)
    declare.exclusive should be (false)
    declare.args should be (args)
    assert(declare.isInstanceOf[QueueDeclare])
  }


   it should "get created as an  exclusive, autodelete, non durable one when only name is supplied" in {
    val declare = QueueDeclare(Some("name"))
    declare.name should be (Some("name"))
    declare.autoDelete should be (true)
    declare.durable should be (false)
    declare.exclusive should be (true)
    declare.args should be (Map())
    assert(declare.isInstanceOf[QueueDeclare])
  }

  it should "have the correct name when declared as passive" in {
    val passive = QueuePassive(Some("name"))
    passive.name should be(Some("name"))
  }
}
