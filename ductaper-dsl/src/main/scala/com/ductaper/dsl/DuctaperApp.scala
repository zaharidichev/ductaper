package com.ductaper.dsl
import scala.io.Source
import scala.util.Try
import org.slf4j.LoggerFactory

/**
  * Created by zahari on 22/02/2017.
  */
trait DuctaperApp extends App {

  def bootstrap: Unit

  private val _logger = LoggerFactory.getLogger(classOf[DuctaperApp])
  private def logTiming(f: () => Unit): Unit = {
    val start = System.currentTimeMillis()
    f()
    _logger.info("Bootstrapping took " + (System.currentTimeMillis() - start) + " ms.")
  }

  protected def getSplash: Iterator[String] = Try(Source.fromResource("spash.dat").getLines).toOption.getOrElse(Iterator.empty)

  for (l <- getSplash) { _logger.info(l) }
  logTiming(bootstrap _)
}
