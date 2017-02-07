package com.ductaper.core.route

import com.ductaper.core.exchange.Exchange

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

case class BrokerRoutingData(exchange: Exchange, routingKey: RoutingKey)
case class RoutingKey(name: String)

