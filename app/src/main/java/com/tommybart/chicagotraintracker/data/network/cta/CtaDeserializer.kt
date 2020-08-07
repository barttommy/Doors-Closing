package com.tommybart.chicagotraintracker.data.network.cta

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tommybart.chicagotraintracker.data.db.entity.RouteArrivalsInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteArrivals
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.TrainEntry
import com.tommybart.chicagotraintracker.data.models.Location
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.network.cta.response.RouteArrivalsContainer
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.TrainLine
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.internal.extensions.getNullable
import java.lang.reflect.Type

class CtaDeserializer : JsonDeserializer<CtaApiResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CtaApiResponse {

        val apiResponse = json?.asJsonObject
        val container = apiResponse?.get("ctatt")?.asJsonObject

        val transmissionTime = container?.get("tmst")?.asString
        val errorCode = container?.getNullable("errCd")?.asInt
        val errorName = container?.getNullable("errNm")?.asString
        val arrivals = container?.getNullable("eta")?.asJsonArray
        val responseInfo = RouteArrivalsInfoEntry(transmissionTime, errorCode, errorName)

        val routeWithArrivalsList = mutableListOf<RouteArrivals>()
        arrivals?.forEach {
            try {
                val arrival = it.asJsonObject

                val stationId = arrival.get("staId").asInt
                val stationName = arrival.get("staNm").asString
                val runNumber = arrival.get("rn").asInt
                val trainLine = TrainLine.fromValue(arrival.get("rt").asString)!!
                val destinationName = arrival.get("destNm").asString
                val predictionTime = arrival.get("prdt").asString
                val arrivalTime = arrival.get("arrT").asString
                val isApproaching = arrival.get("isApp").asInt == 1
                val isDelayed = arrival.get("isDly").asInt == 1
                val latitude = arrival.getNullable("lat")?.asDouble
                val longitude = arrival.getNullable("lon")?.asDouble
                val bearing = arrival.getNullable("heading")?.asDouble

                val route = RouteEntry(null, stationId, stationName, destinationName, trainLine)
                val location = if (latitude == null || longitude == null) null else Location(latitude, longitude)
                val train = TrainEntry(null, null, stationId, runNumber, trainLine,
                    arrivalTime, predictionTime, isApproaching, isDelayed, bearing, location)
                val routeWithArrivals = RouteArrivals(route, mutableListOf(train))
                addRoute(routeWithArrivals, train, routeWithArrivalsList)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse arrival", e)
            }
        }

        return CtaApiResponse(RouteArrivalsContainer(responseInfo, routeWithArrivalsList))
    }

    private fun addRoute(
        routeArrivals: RouteArrivals,
        train: TrainEntry,
        routeArrivalsList: MutableList<RouteArrivals>
    ) {
        if (routeArrivalsList.contains(routeArrivals)) {
            val index = routeArrivalsList.indexOf(routeArrivals)
            val preExistingRoute = routeArrivalsList[index]
            if (preExistingRoute.arrivals.size < Route.TRAIN_LIMIT) {
                preExistingRoute.arrivals.add(train)
            }
        } else {
            routeArrivalsList.add(routeArrivals)
        }
    }
}