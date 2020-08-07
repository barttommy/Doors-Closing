package com.tommybart.chicagotraintracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommybart.chicagotraintracker.data.db.entity.routearrivalsinfo.ROUTE_ARRIVALS_INFO_ID
import com.tommybart.chicagotraintracker.data.db.entity.routearrivalsinfo.RouteArrivalsInfoEntry

@Dao
interface RouteArrivalsInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(routeArrivalsInfoEntry: RouteArrivalsInfoEntry)

    @Query("SELECT * FROM route_arrivals_info WHERE id = $ROUTE_ARRIVALS_INFO_ID")
    fun getResponseInfo(): LiveData<RouteArrivalsInfoEntry>

    @Query("SELECT * FROM route_arrivals_info WHERE id = $ROUTE_ARRIVALS_INFO_ID")
    fun getResponseInfoSync(): RouteArrivalsInfoEntry?
}