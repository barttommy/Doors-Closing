package com.tommybart.chicagotraintracker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val STATION_INFO_ID = 0

@Entity(tableName = "station_info")
data class StationInfoEntry(
    val lastFetchTime: String
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = STATION_INFO_ID
}