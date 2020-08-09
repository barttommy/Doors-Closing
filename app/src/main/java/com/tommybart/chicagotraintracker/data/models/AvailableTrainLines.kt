package com.tommybart.chicagotraintracker.data.models

import com.tommybart.chicagotraintracker.internal.TrainLine
import java.io.Serializable
import java.util.*

data class AvailableTrainLines(
    val red: Boolean,
    val blue: Boolean,
    val brown: Boolean,
    val green: Boolean,
    val orange: Boolean,
    val pink: Boolean,
    val purple: Boolean,
    val purpleExpress: Boolean,
    val yellow: Boolean
) : Serializable {
    val values: EnumMap<TrainLine, Boolean> = TrainLine
        .values()
        .zip(listOf(red, blue, brown, green, orange, pink, purple, purpleExpress, yellow))
        .toMap(EnumMap(TrainLine::class.java))
}
