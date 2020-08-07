package com.tommybart.chicagotraintracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommybart.chicagotraintracker.data.db.entity.STATION_INFO_ID
import com.tommybart.chicagotraintracker.data.db.entity.StationInfoEntry

@Dao
interface StationInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(stationInfoEntry: StationInfoEntry)

    @Query("SELECT * FROM station_info WHERE id = $STATION_INFO_ID")
    fun getStationInfoSync(): StationInfoEntry?
}