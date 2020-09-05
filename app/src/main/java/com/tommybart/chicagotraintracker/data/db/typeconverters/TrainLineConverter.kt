package com.tommybart.chicagotraintracker.data.db.typeconverters

import androidx.room.TypeConverter
import com.tommybart.chicagotraintracker.internal.TrainLine

class TrainLineConverter {
    @TypeConverter
    fun toTrainLine(value: String): TrainLine = TrainLine.valueOf(value)

    @TypeConverter
    fun fromTrainLine(value: TrainLine): String = value.name
}