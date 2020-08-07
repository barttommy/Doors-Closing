package com.tommybart.chicagotraintracker.data.network.cta.response


import com.tommybart.chicagotraintracker.data.db.entity.routearrivalsinfo.RouteArrivalsInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.route.RouteArrivals

data class RouteArrivalsContainer(
    val routeArrivalsInfo: RouteArrivalsInfoEntry,
    val routeArrivalsList: MutableList<RouteArrivals>
)