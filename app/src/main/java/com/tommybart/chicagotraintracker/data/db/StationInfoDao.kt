package com.tommybart.chicagotraintracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommybart.chicagotraintracker.data.db.entity.STATION_INFO_ID
import com.tommybart.chicagotraintracker.data.db.entity.StationInfoEntry
import org.threeten.bp.LocalDateTime

@Dao
interface StationInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(stationInfoEntry: StationInfoEntry)

    @Query("UPDATE station_info SET lastUpdateCheckDate = :newUpdateCheckDate WHERE id = $STATION_INFO_ID")
    fun updateLastUpdateCheckDate(newUpdateCheckDate: LocalDateTime)

    @Query("SELECT * FROM station_info WHERE id = $STATION_INFO_ID")
    fun getStationInfoSync(): StationInfoEntry?
}