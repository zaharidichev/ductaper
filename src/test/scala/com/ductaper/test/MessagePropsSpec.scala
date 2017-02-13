/*
package com.ductaper.test

import java.util.Date

import com.ductaper.core.message.DeliveryMode.Persistent
import com.ductaper.core.message.{Key, MessageProps}
import com.ductaper.core.message.Key._
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
class MessagePropsSpec extends FlatSpec with Matchers {

  "MessageProps" should "to convertible to a AMQP.BasicProperties" in {
    val date: Date = new Date()
    val mp = MessageProps(
      ContentType → "content type",
      ContentEncoding → "encoding",
      Type → "type",
      Timestamp → date,
      MessageId → "message id",
      ReplyTo → "reply to",
      Key.DeliveryMode → Persistent,
      UserId → "user id",
      Expiration → "expiration",
      Priority → 2,
      Headers → Map(),
      CorrelationId → "correlation id",
      AppId → "app id"
    )
    val bp = mp.toJavaBasicProps
    bp.getContentType should be ("content type")
    bp.getContentEncoding should be ("encoding")
    bp.getType should be ("type")
    bp.getTimestamp should be (date)
    bp.getMessageId should be ("message id")
    bp.getReplyTo should be ("reply to")
    bp.getDeliveryMode should be (2)
    bp.getUserId should be ("user id")
    bp.getExpiration should be ("expiration")
    bp.getPriority should be (2)
    bp.getHeaders should be (new java.util.HashMap)
    bp.getCorrelationId should be ("correlation id")
    bp.getAppId should be ("app id")
  }

  it should "extract or null" in {
    val mp = MessageProps(
      ContentType → "content type",
      ContentEncoding → "encoding"
    )
    mp.getOrNull(ContentEncoding) should be ("encoding")
    assert(mp.getOrNull(Priority) == null)
  }

  it should "be appendable" in {
    val mp1 = MessageProps(
      ContentType → "content type"
    )
    val mp2 = MessageProps(
      ContentEncoding → "encoding"
    )

    mp1 ++ mp2 should be (MessageProps(
      ContentType → "content type",
      ContentEncoding → "encoding"
    ))
  }

}

*/
