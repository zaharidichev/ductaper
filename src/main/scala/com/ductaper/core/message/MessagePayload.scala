package com.ductaper.core.message

import java.nio.charset.Charset

import com.ductaper.core.serialization.MessageSerialization

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */
class MessagePayload(data: Array[Byte]) {

  def this() = this(Array.emptyByteArray)

  private val _bytes: Array[Byte] = data.clone()

  def length: Int = data.length

  def toArray: Array[Byte] = _bytes.clone()

  def asString(charset: Charset): String = new String(_bytes, charset)

  def asString: String = asString(Charset.defaultCharset())

}

object MessagePayload {
  def apply(bytes: Byte*): MessagePayload = new MessagePayload(bytes.toArray)
  def apply(array: Array[Byte]): MessagePayload = new MessagePayload(array)
  def apply(string: String, charset: Charset): MessagePayload = new MessagePayload(string.getBytes(charset))
  def apply(string: String): MessagePayload = new MessagePayload(string.getBytes(Charset.defaultCharset()))
  def apply[T](toSerialize: T)(implicit converter: MessageSerialization): MessagePayload = converter.serializeToPayload(toSerialize)
}

