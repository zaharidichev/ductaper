package com.ductaper.core.container

import com.ductaper.core.dsl.{ConcatenatedEndpointDefinition, EndpointDefinition}
import com.ductaper.core.serialization.MessageConverter

import scala.util.{Failure, Success}

/**
  * Created by zahari on 14/02/2017.
  */
trait EndpointDefinitionProcessor {
  def processEndpointDefinitions(endpointDefinitions: Seq[EndpointDefinition])(implicit converter: MessageConverter): Unit
  def processEndpointDefinitions(endpointDefinitions: ConcatenatedEndpointDefinition)(implicit converter: MessageConverter): Unit =
    processEndpointDefinitions(endpointDefinitions.endpoints)
}
