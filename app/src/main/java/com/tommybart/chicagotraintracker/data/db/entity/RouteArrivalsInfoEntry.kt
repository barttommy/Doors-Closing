package com.tommybart.chicagotraintracker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

const val ROUTE_ARRIVALS_INFO_ID = 0

@Entity(tableName = "route_arrivals_info")
data class RouteArrivalsInfoEntry(
    val transmissionTime: LocalDateTime,
    val errorCode: Int?,
    val errorName: String?
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = ROUTE_ARRIVALS_INFO_ID
}