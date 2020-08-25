package com.tommybart.chicagotraintracker.data.db.entity.statearrivals

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "state_info_data")
data class StateInfoEntry(
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    val lastRequestMapIds: List<Int>,
    val transmissionTime: LocalDateTime
)