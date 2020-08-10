package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority

import com.tommybart.chicagotraintracker.data.db.entity.RouteArrivalsInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteArrivals

data class CtaApiResponse(
    val routeArrivalsInfo: RouteArrivalsInfoEntry,
    val routeArrivalsList: List<RouteArrivals>
)