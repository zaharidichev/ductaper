package com.ductaper

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

case class Exchange(name: String) {
  def routingData(routingKey: RoutingKey):BrokerRoutingData = new BrokerRoutingData(this,routingKey)
}

