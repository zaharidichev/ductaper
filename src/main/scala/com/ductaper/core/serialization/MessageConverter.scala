package com.ductaper.core.serialization

import com.ductaper.core.message.MessagePayload

/**
 * Created by zahari on 06/02/2017.
 */
trait MessageConverter {
  def toPayload[T](toSerialize: T): MessagePayload
  def fromPayload[T](toDeserialize: MessagePayload)(implicit m: Manifest[T]): T
}