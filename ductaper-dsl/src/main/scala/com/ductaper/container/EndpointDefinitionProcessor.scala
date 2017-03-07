package com.ductaper.dsl.container

import com.ductaper.core.serialization.MessageConverter
import com.ductaper.dsl.{ConcatenatedEndpointDefinition, EndpointDefinition}

/**
  * Created by zahari on 14/02/2017.
  */
trait EndpointDefinitionProcessor {
  def processEndpointDefinitions(endpointDefinitions: Seq[EndpointDefinition])(implicit converter: MessageConverter): Unit
  def processEndpointDefinitions(endpointDefinitions: ConcatenatedEndpointDefinition)(implicit converter: MessageConverter): Unit =
    processEndpointDefinitions(endpointDefinitions.endpoints)

}
