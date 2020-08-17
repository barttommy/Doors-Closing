package com.tommybart.chicagotraintracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteArrivals
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.TrainEntry
import org.threeten.bp.LocalDateTime

@Dao
abstract class RouteArrivalsDao {

    @Transaction
    open fun upsertAllRouteArrivals(routeArrivalsList: List<RouteArrivals>) {
        routeArrivalsList.forEach { routeArrival ->
            val routeId = _upsertRoute(routeArrival.routeEntry)
            upsertArrivalsForRoute(routeId, routeArrival.arrivals)
        }
    }

    private fun upsertArrivalsForRoute(routeId: Long, arrivals: List<TrainEntry>) {
        arrivals.forEach { train ->
            train.routeId = routeId
        }
        _upsertAllArrivals(arrivals)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _upsertRoute(route: RouteEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _upsertAllArrivals(arrivals: List<TrainEntry>)

    @Transaction
    @Query("SELECT * FROM route_data")
    abstract fun getRoutesWithArrivals(): LiveData<List<RouteArrivals>>

    @Query("DELETE FROM train_data WHERE DATETIME(arrivalTime) < DATETIME(:currentDate)")
    abstract fun deleteOldArrivals(currentDate: LocalDateTime): Int

    @Query("DELETE FROM train_data WHERE mapId NOT IN (:stationIds)")
    abstract fun deleteArrivalsAtOldStations(stationIds: List<Int>): Int

    @Query("DELETE FROM route_data WHERE id NOT IN (SELECT routeId FROM train_data)")
    abstract fun deleteRoutesWithoutArrivals(): Int
}