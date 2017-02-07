package com.ductaper.core.exchange

import com.ductaper.core.route.{BrokerRoutingData, RoutingKey}

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

case class Exchange(name: String) {
  def routingData(routingKey: RoutingKey): BrokerRoutingData = new BrokerRoutingData(this, routingKey)
}

