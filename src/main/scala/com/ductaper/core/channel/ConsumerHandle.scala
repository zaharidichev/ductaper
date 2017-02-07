package com.ductaper.core.channel

import com.ductaper.core.CloseCapable
import com.rabbitmq.client.Channel

/**
 * Created by zahari on 06/02/2017.
 */
class ConsumerHandle(channel: Channel, consumerTag: String) extends CloseCapable {
  override def close(): Unit = channel.basicCancel(consumerTag)
}
