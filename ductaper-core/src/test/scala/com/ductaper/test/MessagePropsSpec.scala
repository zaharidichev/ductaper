
package com.ductaper.test

import java.util.Date

import com.ductaper.core.exchange.DirectExchange
import com.ductaper.core.message.DeliveryMode.Persistent
import com.ductaper.core.message.{Key, MessageProps}
import com.ductaper.core.message.Key._
import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
class MessagePropsSpec extends FlatSpec with Matchers {

  "MessageProps" should "to convertible to a AMQP.BasicProperties" in {
    val date: Date = new Date()
    val replyTo = BrokerRoutingData(DirectExchange("ex"),RoutingKey("key"))
    val mp = MessageProps().contentType("content type")
      .contentEncoding("encoding")
      .messageType("type")
      .timestamp(date)
      .messageId("message id")
      .replyTo(replyTo)
      .deliveryMode(Persistent)
      .userId("user id")
      .expiration("expiration")
      .priority(2)
      .headers(Map())
      .correlationId("correlation id")
      .appId("app id")


    val bp = mp.toJavaBasicProps
    bp.getContentType should be ("content type")
    bp.getContentEncoding should be ("encoding")
    bp.getType should be ("type")
    bp.getTimestamp should be (date)
    bp.getMessageId should be ("message id")
    bp.getReplyTo should be (replyTo.routingKey.name)
    bp.getDeliveryMode should be (2)
    bp.getUserId should be ("user id")
    bp.getExpiration should be ("expiration")
    bp.getPriority should be (2)
    bp.getHeaders should be (new java.util.HashMap)
    bp.getCorrelationId should be ("correlation id")
    bp.getAppId should be ("app id")
  }

  it should "get correct value back" in {
    val mp = MessageProps().contentType("content type").contentEncoding("encoding")
    mp.property(ContentEncoding) should be (Some("encoding"))
  }


  it should "get correct empty option for missing value" in {
    val mp = MessageProps()
    mp.property(Priority) should be (None)
  }


}


