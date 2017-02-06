package com.ductaper

/**
 * @author Zahari Dichev <zaharidichev@gmail.com>.
 */

case class BrokerRoutingData(exchange: Exchange, routingKey: RoutingKey)
case class RoutingKey(name: String)


