package com.ductaper.core.message

import java.nio.charset.Charset
import com.ductaper.core.serialization.MessageConverter

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

  def canEqual(other: Any): Boolean = other.isInstanceOf[MessagePayload]

  override def equals(other: Any): Boolean = other match {
    case that: MessagePayload =>
      (that canEqual this) &&
        _bytes == that._bytes
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(_bytes)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object MessagePayload {
  def apply(bytes: Byte*): MessagePayload = new MessagePayload(bytes.toArray)
  def apply(array: Array[Byte]): MessagePayload = new MessagePayload(array)
  def apply(string: String, charset: Charset): MessagePayload = new MessagePayload(string.getBytes(charset))
  def apply(string: String): MessagePayload = new MessagePayload(string.getBytes(Charset.defaultCharset()))
  def apply[T](toSerialize: T)(implicit converter: MessageConverter): MessagePayload = converter.toPayload(toSerialize)
}
