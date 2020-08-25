package com.tommybart.chicagotraintracker.data.db.entity.statearrivals

import androidx.room.Embedded
import androidx.room.Relation
import com.tommybart.chicagotraintracker.data.models.Location
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Train
import com.tommybart.chicagotraintracker.internal.TrainLine
import org.threeten.bp.LocalDateTime

/*
 * StateArrivals is a one to many relation between StateInfoEntry and Arrival Entry. This definition
 * allows for separate caching of arrival data based off of the state it was requested from. This
 * helps reduce the amount of API requests in case the user is moving through states quickly.
 *
 * There are currently three arrival states: Location, Default, and Search.
 */
data class StateArrivals(
    @Embedded
    val stateInfoEntry: StateInfoEntry,
    @Relation(parentColumn = "id", entityColumn = "stateId")
    val arrivals: List<ArrivalEntry>
) {

    // Converts an ArrivalEntry.kt to fit custom Route model. See Route.java and Train.java
    fun toRouteList(): List<Route> {
        val routeList = mutableListOf<Route>()
        arrivals.forEach { arrival ->
            val trainLine = TrainLine.fromValue(arrival.trainLine)
            val location =
                if (arrival.latitude == null || arrival.longitude == null) null
                else Location(arrival.latitude, arrival.longitude)
            val train = Train(
                arrival.mapId,
                arrival.runNumber,
                trainLine,
                LocalDateTime.parse(arrival.predictionTime),
                LocalDateTime.parse(arrival.arrivalTime),
                arrival.isApproaching == 1,
                arrival.isDelayed == 1,
                arrival.bearing,
                location
            )
            val route = Route(
                arrival.mapId,
                arrival.stationName,
                arrival.destinationName,
                trainLine,
                arrayListOf(train)
            )
            addRoute(route, train, routeList)
        }
        return routeList
    }

    // Add route if it does not already exist. If it does exist, add the train to the existing
    // route's collection of trains.
    private fun addRoute(route: Route, train: Train, routeList: MutableList<Route>) {
        if (routeList.contains(route)) {
            val index = routeList.indexOf(route)
            val preExistingRoute = routeList[index]
            if (preExistingRoute.arrivals.size < Route.TRAIN_LIMIT) {
                preExistingRoute.arrivals.add(train)
            }
        } else if (isRouteUnique(route, routeList)) {
            routeList.add(route)
        }
    }

    // Returns true if a similar route does not exist at a closer station.
    private fun isRouteUnique(route: Route, routeList: MutableList<Route>): Boolean {
        routeList.forEach {
            if (it.trainLine == route.trainLine && it.destinationName == route.destinationName) {
                return false
            }
        }
        return true
    }
}