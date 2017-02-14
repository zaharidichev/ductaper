package com.ductaper.core.dsl



trait EndpointDefinition {
  def endpointRoute: EndpointRoute
  def ~(that:EndpointDefinition):ConcatenatedEndpointDefinition  = ConcatenatedEndpointDefinition(List(this,that))
}

case class ConcatenatedEndpointDefinition(endpoints:List[EndpointDefinition]) {
  def ~(that:ConcatenatedEndpointDefinition):ConcatenatedEndpointDefinition  =
    ConcatenatedEndpointDefinition(this.endpoints.:::(that.endpoints))

  def ~(that:EndpointDefinition):ConcatenatedEndpointDefinition  =
    ConcatenatedEndpointDefinition(this.endpoints.::(that))
}


//type InputOutputFunctor[-T,+R] = T => R
//type NoInputOutputFunctor[+R] = () => R
//type InputNoOutputFunctor[-T] = T => Unit
//type NoInputNoOutputFunctor = () => Unit

case class InputOutputEndpointDefinition[T: Manifest, R: Manifest](
  override val endpointRoute: EndpointRoute,
  functor: T => R, inputManifest: Manifest[T],
  outputManifest: Manifest[R]
) extends EndpointDefinition

case class NoInputOutputEndpointDefinition[R: Manifest](
  override val endpointRoute: EndpointRoute,
  functor: () => R,
  outputManifest: Manifest[R]
) extends EndpointDefinition

case class InputNoOutputEndpointDefinition[T: Manifest](
  override val endpointRoute: EndpointRoute,
  functor: T => Unit,
  inputManifest: Manifest[T]
) extends EndpointDefinition

case class NoInputNoOutputEndpointDefinition(
  override val endpointRoute: EndpointRoute,
  functor: () => Unit
) extends EndpointDefinition

