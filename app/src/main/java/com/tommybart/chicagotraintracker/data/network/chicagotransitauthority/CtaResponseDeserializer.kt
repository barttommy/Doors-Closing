package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tommybart.chicagotraintracker.data.db.entity.RouteArrivalsInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteArrivals
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.TrainEntry
import com.tommybart.chicagotraintracker.data.models.Location
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.internal.TrainLine
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.internal.extensions.getNullable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.lang.reflect.Type

class CtaResponseDeserializer : JsonDeserializer<CtaApiResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CtaApiResponse {
        val apiResponse = json?.asJsonObject
        val container = apiResponse?.get("ctatt")?.asJsonObject

        val routeArrivalsInfoEntry = RouteArrivalsInfoEntry(
            LocalDateTime.parse(container?.get("tmst")?.asString)
                ?: ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime(),
            container?.getNullable("errCd")?.asInt,
            container?.getNullable("errNm")?.asString
        )

        val arrivals = container?.getNullable("eta")?.asJsonArray
        val routeWithArrivalsList = mutableListOf<RouteArrivals>()

        arrivals?.forEach {
            try {
                val arrival = it.asJsonObject
                val mapId = arrival.get("staId").asInt
                val trainLine = TrainLine.fromValue(arrival.get("rt").asString)!!
                val latitude = arrival.getNullable("lat")?.asDouble
                val longitude = arrival.getNullable("lon")?.asDouble

                val routeEntry = RouteEntry(
                    null,
                    mapId,
                    arrival.get("staNm").asString,
                    arrival.get("destNm").asString,
                    trainLine
                )

                val location =
                    if (latitude == null || longitude == null) null
                    else Location(latitude, longitude)

                val trainEntry = TrainEntry(
                    null,
                    null,
                    mapId,
                    arrival.get("rn").asInt,
                    trainLine,
                    LocalDateTime.parse(arrival.get("prdt").asString),
                    LocalDateTime.parse(arrival.get("arrT").asString),
                    arrival.get("isApp").asInt == 1,
                    arrival.get("isDly").asInt == 1,
                    arrival.getNullable("heading")?.asDouble,
                    location
                )

                val routeWithArrivals = RouteArrivals(routeEntry, mutableListOf(trainEntry))
                addRouteEntry(routeWithArrivals, trainEntry, routeWithArrivalsList)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse arrival", e)
            }
        }

        return CtaApiResponse(routeArrivalsInfoEntry, routeWithArrivalsList)
    }

    private fun addRouteEntry(
        routeArrivals: RouteArrivals,
        trainEntry: TrainEntry,
        routeArrivalsList: MutableList<RouteArrivals>
    ) {
        if (routeArrivalsList.contains(routeArrivals)) {
            val index = routeArrivalsList.indexOf(routeArrivals)
            val preExistingRoute = routeArrivalsList[index]
            if (preExistingRoute.arrivals.size < Route.TRAIN_LIMIT) {
                preExistingRoute.arrivals.add(trainEntry)
            }
        } else {
            addUniqueRoutes(routeArrivals, routeArrivalsList)
        }
    }

    private fun addUniqueRoutes(
        routeArrivals: RouteArrivals,
        routeArrivalsList: MutableList<RouteArrivals>
    ) {
        routeArrivalsList.forEach {
            if (it.routeEntry.trainLine == routeArrivals.routeEntry.trainLine
                && it.routeEntry.destinationName == routeArrivals.routeEntry.destinationName
            ) {
                return
            }
        }
        routeArrivalsList.add(routeArrivals)
    }
}