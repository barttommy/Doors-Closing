package com.tommybart.chicagotraintracker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val ROUTE_ARRIVALS_REQUEST_ID = 0

@Entity(tableName = "route_arrivals_request")
data class RouteArrivalsRequestEntry(
    val lastRequest: List<Int>
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = ROUTE_ARRIVALS_REQUEST_ID
}