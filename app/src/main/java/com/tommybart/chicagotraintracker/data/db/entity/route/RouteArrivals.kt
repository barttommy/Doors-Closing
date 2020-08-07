package com.tommybart.chicagotraintracker.data.db.entity.route

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Train

data class RouteArrivals(
    @Embedded
    val routeEntry: RouteEntry,
    @Relation(parentColumn = "id", entityColumn = "routeId")
    val arrivals: MutableList<TrainEntry>
) {

    fun toRoute(): Route {
        val arrivals = convertArrivals()
        return Route(
            routeEntry.stationId,
            routeEntry.stationName,
            routeEntry.destinationName,
            routeEntry.trainLine,
            arrivals
        )
    }

    private fun convertArrivals(): ArrayList<Train> {
        val conversionResult = arrayListOf<Train>()
        arrivals.forEach { trainEntry ->
            conversionResult.add(Train(
                trainEntry.routeId ?: -1,
                trainEntry.runNumber,
                trainEntry.stationId,
                trainEntry.trainLine,
                trainEntry.predictionTime,
                trainEntry.arrivalTime,
                trainEntry.isApproaching,
                trainEntry.isDelayed,
                trainEntry.bearing,
                trainEntry.location
            ))
        }
        return conversionResult
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        else if (other !is RouteArrivals) return false
        return routeEntry == other.routeEntry
    }

    @Ignore
    private var hashCode: Int = 0
    override fun hashCode(): Int {
        if (hashCode == 0) {
            hashCode = 17
            hashCode = 37 * hashCode + routeEntry.hashCode()
        }
        return hashCode
    }
}