package com.tommybart.chicagotraintracker.data.db.entity

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class RouteWithArrivals(
    @Embedded
    val route: RouteEntry,
    @Relation(parentColumn = "id", entityColumn = "routeId")
    val arrivals: MutableList<TrainEntry>
) {

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        else if (other !is RouteWithArrivals) return false
        return route == other.route
    }

    @Ignore
    private var hashCode: Int = 0
    override fun hashCode(): Int {
        if (hashCode == 0) {
            hashCode = 17
            hashCode = 37 * hashCode + route.hashCode()
        }
        return hashCode
    }
}