package com.ductaper.core.container

import com.ductaper.core.dsl.EndpointDefinition
import com.ductaper.core.serialization.MessageConverter

import scala.util.{Failure, Success}

/**
  * Created by zahari on 14/02/2017.
  */
trait EndpointDefinitionProcessor {
  def processEndpointDefinitions(seq: Seq[EndpointDefinition])(implicit converter: MessageConverter): Unit
}
