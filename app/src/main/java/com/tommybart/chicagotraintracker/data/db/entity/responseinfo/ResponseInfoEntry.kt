package com.tommybart.chicagotraintracker.data.db.entity.responseinfo

import androidx.room.Entity
import androidx.room.PrimaryKey

const val RESPONSE_INFO_ID = 0

@Entity(tableName = "response_info")
data class ResponseInfoEntry(
    val transmissionTime: String?,
    val errorCode: Int?,
    val errorName: String?
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = RESPONSE_INFO_ID
}