package com.ductaper.core.serialization

/**
 * Created by zahari on 07/02/2017.
 */
import com.ductaper.core.message.MessagePayload
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JsonMessageConverter$ extends MessageConverter {

  val smileFactory = new SmileFactory()
  val mapper = new ObjectMapper(smileFactory) with ScalaObjectMapper

  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def toJson(value: Any): Array[Byte] = {
    mapper.writeValueAsBytes(value)
  }

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = {
    mapper.readValue[T](json)
  }
  override def toPayload[T](toSerialize: T): MessagePayload = MessagePayload(toJson(toSerialize))
  override def fromPayload[T](toDeserialize: MessagePayload)(implicit m: Manifest[T]): T = mapper.readValue[T](toDeserialize.toArray)

  implicit val converter: MessageConverter = this
}

