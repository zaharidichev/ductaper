package com.ductaper.core.serialization

/**
 * Created by zahari on 07/02/2017.
 */
import com.ductaper.core.message.MessagePayload
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JsonMessageConvertor extends MessageSerialization {
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def toJson(value: Any): String = {
    mapper.writeValueAsString(value)
  }

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = {
    mapper.readValue[T](json)
  }
  override def serializeToPayload[T](toSerialize: T): MessagePayload = MessagePayload(toJson(toSerialize))
  override def deserializeFromPayload[T](toDeserialize: MessagePayload)(implicit m: Manifest[T]): T = mapper.readValue[T](toDeserialize.asString)

  implicit val converter: MessageSerialization = this
}

