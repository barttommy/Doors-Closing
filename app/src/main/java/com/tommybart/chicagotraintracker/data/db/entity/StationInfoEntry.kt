package com.tommybart.chicagotraintracker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

const val STATION_INFO_ID = 0

@Entity(tableName = "station_info")
data class StationInfoEntry(
    val lastFetchDate: LocalDate,
    val lastUpdateCheckDate: LocalDate
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = STATION_INFO_ID
}