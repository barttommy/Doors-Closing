package com.tommybart.chicagotraintracker.data.network.cta.response


import com.tommybart.chicagotraintracker.data.models.Route
import org.threeten.bp.ZonedDateTime

data class ArrivalsContainer(
    val transmissionTime: ZonedDateTime,
    val errorCode: Int?,
    val errorName: String?,
    val routeList: List<Route>
)