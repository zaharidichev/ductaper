package com.ductaper.core.configuration
import scala.util.{Failure, Properties, Success, Try}
import com.typesafe.config.ConfigFactory

object ConfigurationEnvConstants {

  val _uri: String = "rabbit.Uri"
  val _userName: String = "rabbit.username"
  val _password: String = "rabbit.password"
  val _port: String = "rabbit.port"
  val _requestedChannelMax: String = "rabbit.requestedChannelMax"
  val _requestedHeartbeat: String = "rabbit.requestedHeartbeat"
  val _requestedFrameMax: String = "rabbit.requestedFrameMax"
  val _connectionTimeout: String = "rabbit.connectionTimeout"
  val _shutdownTimeout: String = "rabbit.shutdownTimeout"
  val _topologyRecovery: String = "rabbit.topologyRecovery"
  val _reconnectionStrategy: String = "rabbit.reconnectionStrategy"

  def toCapitalSnakeCase(v: String): String = {

    def camel2Underscore(text: String) = text.drop(1).foldLeft(text.headOption.map(_.toLower + "") getOrElse "") {
      case (acc, c) if c.isUpper => acc + "_" + c.toLower
      case (acc, c) => acc + c
    }

    val prefix = v.split('.')(0).toUpperCase
    val suffix = camel2Underscore(v.split('.')(1)).toUpperCase
    prefix + "_" + suffix
  }
}

/**
  * Created by zahari on 16/02/2017.
  */
trait ConnectionConfiguration {

  private final val AMQP_PREFIX = "amqp://"

  private def addAMQPPrefixIfNeeded(h: String): String = if (h.contains(AMQP_PREFIX)) h else AMQP_PREFIX + h

  def uri: String = getValForConfProp(ConfigurationEnvConstants._uri).map(addAMQPPrefixIfNeeded).getOrElse("amqp://localhost")
  def userName: String = getValForConfProp(ConfigurationEnvConstants._userName).getOrElse("guest")
  def password: String = getValForConfProp(ConfigurationEnvConstants._password).getOrElse("guest")
  def port: Option[Int] = getValForConfProp(ConfigurationEnvConstants._port)
  def requestedChannelMax: Option[Int] = getValForConfProp(ConfigurationEnvConstants._requestedChannelMax)
  def requestedFrameMax: Option[Int] = getValForConfProp(ConfigurationEnvConstants._requestedFrameMax)
  def requestedHeartbeat: Option[Int] = getValForConfProp(ConfigurationEnvConstants._requestedHeartbeat)
  def connectionTimeout: Option[Int] = getValForConfProp(ConfigurationEnvConstants._connectionTimeout)
  def shutdownTimeout: Option[Int] = getValForConfProp(ConfigurationEnvConstants._shutdownTimeout)
  def topologyRecovery: Option[Boolean] = getValForConfProp(ConfigurationEnvConstants._topologyRecovery)
  protected def getValForConfProp[T](key: String): Option[T]
}

class EnvPropertyConnectionConfiguration extends ConnectionConfiguration {
  override protected def getValForConfProp[T](key: String): Option[T] = {
    Properties.envOrNone(ConfigurationEnvConstants.toCapitalSnakeCase(key)).map(x => x.asInstanceOf[T])
  }
}

object EnvPropertyConnectionConfiguration {
  def apply: ConnectionConfiguration = new EnvPropertyConnectionConfiguration()
}

class FileBasedConnectionConfiguration extends ConnectionConfiguration {

  private val configFactory = ConfigFactory.load()

  override protected def getValForConfProp[T](key: String): Option[T] = {
    Try(configFactory.getString(key)) match {
      case Success(value) => Some(value.asInstanceOf[T])
      case Failure(t) => None
    }

  }
}

object FileBasedConnectionConfiguration {
  def apply: ConnectionConfiguration = new FileBasedConnectionConfiguration()
}
