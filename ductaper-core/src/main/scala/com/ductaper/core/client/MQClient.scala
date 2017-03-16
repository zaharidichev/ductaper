package com.ductaper.core.client

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import com.ductaper.core.message.MessageProps
import com.ductaper.core.route.BrokerRoutingData
import com.ductaper.core.serialization.MessageConverter

/**
  * Created by zahari on 20/02/2017.
  */
trait MQClient {
  def send[T](data: T, routingData: BrokerRoutingData, messageProps: MessageProps)
             (implicit converter: MessageConverter, executionContext: ExecutionContext): Unit

  def sendAndReceive[T, R](data: T, routingData: BrokerRoutingData, messageProps: MessageProps, timeout: Duration)
                         (implicit converter: MessageConverter, responseManifest: Manifest[R], executionContext: ExecutionContext): Future[Try[R]]
  }
