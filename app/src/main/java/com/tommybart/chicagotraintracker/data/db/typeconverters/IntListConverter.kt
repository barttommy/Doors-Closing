package com.tommybart.chicagotraintracker.data.db.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson

class IntListConverter {

    @TypeConverter
    fun toList(value: String): List<Int> =
        Gson().fromJson(value, Array<Int>::class.java).toList()

    @TypeConverter
    fun fromList(value: List<Int>): String =
        Gson().toJson(value)
}