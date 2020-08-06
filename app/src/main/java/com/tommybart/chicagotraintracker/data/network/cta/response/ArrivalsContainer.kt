package com.tommybart.chicagotraintracker.data.network.cta.response


import com.tommybart.chicagotraintracker.data.db.entity.RouteWithArrivals
import org.threeten.bp.ZonedDateTime

data class ArrivalsContainer(
    val transmissionTime: String?,
    val errorCode: Int?,
    val errorName: String?,
    val routeWithArrivalsList: MutableList<RouteWithArrivals>
)