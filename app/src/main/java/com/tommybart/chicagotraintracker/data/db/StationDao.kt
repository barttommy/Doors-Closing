package com.tommybart.chicagotraintracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommybart.chicagotraintracker.data.db.entity.station.StationEntry

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(stationEntries: List<StationEntry>)

    @Query("SELECT * FROM station_data")
    fun getStationData(): LiveData<List<StationEntry>>
}