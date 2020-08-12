package com.tommybart.chicagotraintracker.data.db.typeconverters

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class LocalDateConverter {

    @TypeConverter
    fun toDate(value: String): LocalDate =
        LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)

    @TypeConverter
    fun fromDate(value: LocalDate): String =
        value.format(DateTimeFormatter.ISO_LOCAL_DATE)
}