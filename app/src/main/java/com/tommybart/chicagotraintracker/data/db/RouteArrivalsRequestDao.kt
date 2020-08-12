package com.tommybart.chicagotraintracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommybart.chicagotraintracker.data.db.entity.ROUTE_ARRIVALS_REQUEST_ID
import com.tommybart.chicagotraintracker.data.db.entity.RouteArrivalsRequestEntry

@Dao
interface RouteArrivalsRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(routeArrivalsRequestEntry: RouteArrivalsRequestEntry)

    @Query("SELECT * FROM route_arrivals_request WHERE id = $ROUTE_ARRIVALS_REQUEST_ID")
    fun getLastRequestSync(): RouteArrivalsRequestEntry?
}