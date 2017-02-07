package com.ductaper.core.container

/**
 * Created by zahari on 07/02/2017.
 */
sealed trait DuctaperRouteResult[+T]

object DuctaperRouteResult {
  final case class Result[T](response: T) extends DuctaperRouteResult[T]
  final case object NoResult extends DuctaperRouteResult[Nothing]
  final case class Error[Throwable](t: Throwable) extends DuctaperRouteResult[Throwable]
}

