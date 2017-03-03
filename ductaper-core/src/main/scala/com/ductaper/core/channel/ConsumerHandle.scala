package com.ductaper.core.channel

import com.ductaper.core.misc.CloseCapable
import com.ductaper.core.thinwrappers.ChannelThinWrapper
import com.rabbitmq.client.Channel

/**
 * Created by zahari on 06/02/2017.
 */
class ConsumerHandle(channel: ChannelThinWrapper, consumerTag: String) extends CloseCapable {
  override def close(): Unit = channel.basicCancel(consumerTag)
}
