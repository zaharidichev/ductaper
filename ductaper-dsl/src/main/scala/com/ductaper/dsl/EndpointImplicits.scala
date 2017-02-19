package com.ductaper.dsl

import com.ductaper.dsl.EndpointTypes.{broadcast_endpoint, unicast_endpoint}

import scala.language.implicitConversions

/**
  * Created by zahari on 08/02/2017.
  */
object EndpointImplicits {



  type WithUnicastRoute = (EndpointType,UnicastEndpointRoute,Int)
  type WithBroadcastRoute = (EndpointType,BroadCastEndpointRoute,Int)
  type WithRoute = (EndpointType,EndpointRoute,Int)

  //type InputOutputFunctor[-T,+R] = T => R
  //type NoInputOutputFunctor[+R] = () => R

  //type InputNoOutputFunctor[-T] = T => Unit
  //type NoInputNoOutputFunctor = () => Unit


  def input[T](implicit manifest: Manifest[T]):Manifest[T] = manifest



  trait ConsumingExecution[T] {
    def consumes(func:T => Unit):EndpointDefinition
  }


  trait ProvidingExecution {
    def returns[R:Manifest](func:() => R): EndpointDefinition
  }

  trait FunctionalExecution[T] {
    def returns[R:Manifest](func: T => R): EndpointDefinition
  }

  trait CallingExecution {
    def calls(func: () => Unit): EndpointDefinition
  }





  class BroadcastInputExecutor[T:Manifest](_withRoute: WithBroadcastRoute) extends ConsumingExecution[T]   {
    override def consumes(func: T => Unit): EndpointDefinition = InputNoOutputEndpointDefinition[T](_withRoute._2,_withRoute._3,func,manifest[T])
  }

  class BroadcastNoInputExecutor(_withRoute: WithBroadcastRoute) extends CallingExecution   {
    override def calls(func: () => Unit): EndpointDefinition = NoInputNoOutputEndpointDefinition(_withRoute._2,_withRoute._3,func)
  }


  class UnicastInputExecutor[T:Manifest](_withRoute: WithUnicastRoute) extends ConsumingExecution[T] with FunctionalExecution[T] {
    override def consumes(func: T => Unit): EndpointDefinition = InputNoOutputEndpointDefinition[T](_withRoute._2,_withRoute._3,func,manifest[T])
    override def returns[R:Manifest](func: T => R): EndpointDefinition = InputOutputEndpointDefinition[T,R](_withRoute._2,_withRoute._3,func,manifest[T],manifest[R])
  }

  class UnicastNoInputExecutor(_withRoute: WithUnicastRoute) extends ProvidingExecution with CallingExecution {
    override def returns[R:Manifest](func: () => R): EndpointDefinition = NoInputOutputEndpointDefinition[R](_withRoute._2,_withRoute._3,func,manifest[R])
    override def calls(func: () => Unit): EndpointDefinition = NoInputNoOutputEndpointDefinition(_withRoute._2,_withRoute._3,func)

  }

  class UnicastGluingHelper(withRoute: WithUnicastRoute) {
    def takes[T:Manifest]:UnicastInputExecutor[T] = new UnicastInputExecutor[T](withRoute)
  }


  class BroadcastGluingHelper(withRoute: WithBroadcastRoute) {
    def takes[T:Manifest]:BroadcastInputExecutor[T] = new BroadcastInputExecutor[T](withRoute)
  }

  class RouteHelper[T](val consumers:Int = 1) {

    def consumers(_consumers:Int):RouteHelper[T] = new RouteHelper[T](_consumers)

    def at(route:T):(EndpointType,T,Int) = route match {
        case _:UnicastEndpointRoute => (unicast_endpoint,route,consumers)
        case _:BroadCastEndpointRoute => (broadcast_endpoint,route,consumers)
      }

  }


  implicit def toUnicastRouteHelper(endpointType: UnicastEndpointType):RouteHelper[UnicastEndpointRoute] = new RouteHelper[UnicastEndpointRoute];

  implicit def toBroadcastRouteHelper(endpointType: BroadcastEndpointType):RouteHelper[BroadCastEndpointRoute] = new RouteHelper[BroadCastEndpointRoute];

  implicit def toUnicastGluingHelper(wir:WithUnicastRoute):UnicastGluingHelper = new UnicastGluingHelper(wir)

  implicit def toUnicastNoInputExecutor(wir:WithUnicastRoute):UnicastNoInputExecutor = new UnicastNoInputExecutor(wir)

  implicit def toBroadcastGluingHelper(wir:WithBroadcastRoute):BroadcastGluingHelper = new BroadcastGluingHelper(wir)

  implicit def toBroadcastNoInputexecutor(wir:WithBroadcastRoute):BroadcastNoInputExecutor = new BroadcastNoInputExecutor(wir)



  }

