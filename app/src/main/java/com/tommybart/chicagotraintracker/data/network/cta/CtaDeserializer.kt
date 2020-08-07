package com.tommybart.chicagotraintracker.data.network.cta

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tommybart.chicagotraintracker.data.db.entity.responseinfo.ResponseInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.route.RouteEntry
import com.tommybart.chicagotraintracker.data.db.entity.route.RouteWithArrivals
import com.tommybart.chicagotraintracker.data.db.entity.route.TrainEntry
import com.tommybart.chicagotraintracker.data.models.Location
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.network.cta.response.RouteContainer
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
        val responseInfo = ResponseInfoEntry(transmissionTime, errorCode, errorName)

        val routeWithArrivalsList = mutableListOf<RouteWithArrivals>()
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
                val train = TrainEntry(null, null, stationId, runNumber, trainLine,
                    arrivalTime, predictionTime, isApproaching, isDelayed, bearing,
                    Location(latitude, longitude))
                val routeWithArrivals = RouteWithArrivals(route, mutableListOf(train))
                addRoute(routeWithArrivals, train, routeWithArrivalsList)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse arrival", e)
            }
        }

        return CtaApiResponse(RouteContainer(responseInfo, routeWithArrivalsList))
    }

    private fun addRoute(
        routeWithArrivals: RouteWithArrivals,
        train: TrainEntry,
        routeWithArrivalsList: MutableList<RouteWithArrivals>
    ) {
        if (routeWithArrivalsList.contains(routeWithArrivals)) {
            val index = routeWithArrivalsList.indexOf(routeWithArrivals)
            val preExistingRoute = routeWithArrivalsList[index]
            if (preExistingRoute.arrivals.size < Route.TRAIN_LIMIT) {
                preExistingRoute.arrivals.add(train)
            }
        } else {
            routeWithArrivalsList.add(routeWithArrivals)
        }
    }
}