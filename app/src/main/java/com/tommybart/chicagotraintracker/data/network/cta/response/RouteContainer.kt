package com.tommybart.chicagotraintracker.data.network.cta.response


import com.tommybart.chicagotraintracker.data.db.entity.responseinfo.ResponseInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.route.RouteWithArrivals

data class RouteContainer(
    val responseInfo: ResponseInfoEntry,
    val routeWithArrivalsList: MutableList<RouteWithArrivals>
)