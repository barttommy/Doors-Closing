package com.tommybart.chicagotraintracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommybart.chicagotraintracker.data.db.entity.STATION_INFO_ID
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.data.db.entity.StationInfoEntry

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(stationEntries: List<StationEntry>)

    @Query("SELECT * FROM station_data")
    fun getStationEntries(): LiveData<List<StationEntry>>

    @Query("SELECT * FROM station_data")
    fun getStationEntriesSync(): List<StationEntry>

    @Query("DELETE FROM station_data")
    fun deleteAll(): Int
}