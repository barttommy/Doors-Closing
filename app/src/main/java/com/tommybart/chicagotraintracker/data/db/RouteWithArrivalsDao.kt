package com.tommybart.chicagotraintracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tommybart.chicagotraintracker.data.db.entity.route.RouteEntry
import com.tommybart.chicagotraintracker.data.db.entity.route.RouteWithArrivals
import com.tommybart.chicagotraintracker.data.db.entity.route.TrainEntry
import org.threeten.bp.LocalDateTime

@Dao
abstract class RouteWithArrivalsDao {

    fun upsertArrivalsForRoute(routeId: Long, arrivals: List<TrainEntry>) {
        arrivals.forEach { train ->
            train.routeId = routeId
        }
        _upsertAll(arrivals)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _upsertAll(arrivals: List<TrainEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsertRoute(route: RouteEntry): Long

    @Transaction
    @Query("SELECT * FROM route_data")
    abstract fun getRoutesWithArrivals(): LiveData<List<RouteWithArrivals>>

    @Query("DELETE FROM train_data WHERE DATETIME(arrivalTime) < DATETIME(:currentDate)")
    abstract fun deleteOldArrivals(currentDate: LocalDateTime): Int

    @Query("DELETE FROM train_data WHERE stationId NOT IN (:stationIds)")
    abstract fun deleteArrivalsAtOldStations(stationIds: List<Int>): Int

    @Query("DELETE FROM route_data WHERE id NOT IN (SELECT routeId FROM train_data)")
    abstract fun deleteRoutesWithoutArrivals(): Int
}