package com.tommybart.chicagotraintracker.data.network.cta.response


import com.tommybart.chicagotraintracker.data.db.entity.RouteArrivalsInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteArrivals

data class RouteArrivalsContainer(
    val routeArrivalsInfo: RouteArrivalsInfoEntry,
    val routeArrivalsList: MutableList<RouteArrivals>
)