package com.ductaper.core.serialization

import com.ductaper.core.message.MessagePayload

/**
 * Created by zahari on 06/02/2017.
 */
trait MessageSerialization {
  def serializeToPayload[T](toSerialize: T): MessagePayload
  def deserializeFromPayload[T](toDeserialize: MessagePayload)(implicit m: Manifest[T]): T
}