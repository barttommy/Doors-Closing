package com.tommybart.chicagotraintracker.data.db.entity.route

import androidx.room.*
import com.tommybart.chicagotraintracker.data.models.Location
import com.tommybart.chicagotraintracker.internal.TrainLine
import org.threeten.bp.LocalDateTime

@Entity(tableName = "train_data", indices = [Index(value = ["id", "runNumber"], unique = true)])
data class TrainEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var routeId: Long? = null,
    val stationId: Int,
    val runNumber: Int,
    val trainLine: TrainLine,
    val arrivalTime: String,
    val predictionTime: String,
    val isApproaching: Boolean,
    val isDelayed: Boolean,
    val bearing: Double?,
    @Embedded
    val location: Location?
)