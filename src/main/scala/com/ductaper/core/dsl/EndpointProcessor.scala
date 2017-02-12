package com.ductaper.core.dsl

/**
  * Created by zahari on 11/02/2017.
  */
object EndpointProcessor {

  def processEndpoint[T, R](e: InputOutputEndpointDefinition[T, R])(implicit inputManifest: Manifest[T], outputManifest: Manifest[R]):Unit = {
    println("Endpoint at " + e.endpointRoute + " executing " + inputManifest + " => " + outputManifest)
  }


  def processEndpoint[R](e: NoInputOutputEndpointDefinition[R])(implicit outputManifest: Manifest[R]):Unit = {
    println("Endpoint at " + e.endpointRoute + " executing () => " + outputManifest)
  }

  def processEndpoint[T](e: InputNoOutputEndpointDefinition[T])(implicit inputManifest: Manifest[T]):Unit = {
    println("Endpoint at " + e.endpointRoute + " executing " + inputManifest + " => Unit")

  }

  def processEndpoint(e: NoInputNoOutputEndpointDefinition):Unit = {
    println("Endpoint at " + e.endpointRoute + " executing () => Unit")

  }


  def processEndpointRoutes(seq: Seq[EndpointDefinition]):Unit = seq foreach processEndpointRoute

  def processEndpointRoute(e: EndpointDefinition): Unit = {
    e match {

      case end: InputOutputEndpointDefinition[_, _] => {
        implicit val inputManifest = end.inputManifest
        implicit val outputManifest = end.outputManifest
        processEndpoint(end)
      }


      case end: NoInputOutputEndpointDefinition[_] => {
        implicit val outputManifest = end.outputManifest
        processEndpoint(end)
      }

      case end: InputNoOutputEndpointDefinition[_] => {
        implicit val inputManifest = end.inputManifest
        processEndpoint(end)
      }

      case end: NoInputNoOutputEndpointDefinition => processEndpoint(end)


    }

  }

}
