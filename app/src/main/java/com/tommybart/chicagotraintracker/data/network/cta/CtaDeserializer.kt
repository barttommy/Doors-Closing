package com.tommybart.chicagotraintracker.data.network.cta

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tommybart.chicagotraintracker.data.db.entity.Location
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Train
import com.tommybart.chicagotraintracker.data.network.cta.response.ArrivalsContainer
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.TrainLine
import com.tommybart.chicagotraintracker.internal.extensions.getNullable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.lang.reflect.Type

const val CHICAGO_ZONE_ID = "America/Chicago"

class CtaDeserializer : JsonDeserializer<CtaApiResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CtaApiResponse {

        val apiResponse = json?.asJsonObject
        val arrivalsContainer = apiResponse?.get("ctatt")?.asJsonObject

        val transmissionTime = LocalDateTime.parse(arrivalsContainer?.get("tmst")?.asString)
            .atZone(ZoneId.of(CHICAGO_ZONE_ID))
        val errorCode = arrivalsContainer?.get("errCd")?.asInt
        val errorName = arrivalsContainer?.getNullable("errNm")?.asString
        val arrivals = arrivalsContainer?.get("eta")?.asJsonArray

        val routeList = mutableListOf<Route>()
        arrivals?.forEach {
            val arrival = it.asJsonObject

            val stationId = arrival.get("staId").asInt
            val stationName = arrival.get("staNm").asString
            val trainLine = TrainLine.fromValue(arrival.get("rt").asString)
            val destinationName = arrival.get("destNm").asString
            val predictionTime = LocalDateTime.parse(arrival.get("prdt").asString)
                .atZone(ZoneId.of(CHICAGO_ZONE_ID))
            val arrivalTime = LocalDateTime.parse(arrival.get("arrT").asString)
                .atZone(ZoneId.of(CHICAGO_ZONE_ID))
            val isApproaching = arrival.get("isApp").asInt == 1
            val isDelayed = arrival.get("isDly").asInt == 1
            val latitude = arrival.getNullable("lat")?.asDouble
            val longitude = arrival.getNullable("lon")?.asDouble
            val bearing = arrival.getNullable("heading")?.asDouble

            val train = Train(trainLine, arrivalTime, predictionTime, isApproaching, isDelayed,
                              bearing, Location(latitude, longitude))
            val route = Route(stationId, stationName, destinationName, trainLine,
                              arrayListOf(train))
            addRoute(route, train, routeList)
        }

        return CtaApiResponse(ArrivalsContainer(transmissionTime, errorCode, errorName, routeList))
    }

    private fun addRoute(route: Route, train: Train, routeList: MutableList<Route>) {
        if (routeList.contains(route)) {
            val preExistingRoute = routeList[routeList.indexOf(route)]
            if (preExistingRoute.arrivals.size < Route.TRAIN_LIMIT) {
                preExistingRoute.arrivals.add(train)
            }
        } else {
            routeList.add(route)
        }
    }
}