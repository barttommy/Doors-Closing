package com.tommybart.chicagotraintracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.ArrivalEntry
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.StateArrivals
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.StateInfoEntry
import org.threeten.bp.LocalDateTime

@Dao
abstract class StateArrivalsDao {

    @Transaction
    open fun updateStateArrivals(stateArrivals: StateArrivals) {
        _upsertStateInfo(stateArrivals.stateInfoEntry)
        _deleteArrivals(stateArrivals.stateInfoEntry.id)
        upsertArrivalsForState(stateArrivals.stateInfoEntry.id, stateArrivals.arrivals)
    }

    private fun upsertArrivalsForState(stateId: Int, arrivals: List<ArrivalEntry>) {
        arrivals.forEach { arrival ->
            arrival.stateId = stateId
        }
        _upsertArrivals(arrivals)
    }

    @Transaction
    open fun deleteOldData(
        stateId: Int,
        newRequest: List<Int>,
        currentDateTime: LocalDateTime
    ): Int {
        var deleteCount = _deleteArrivalsAtOldStations(stateId, newRequest)
        deleteCount += _deleteOldArrivals(currentDateTime)
        return deleteCount
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _upsertStateInfo(stateInfoEntry: StateInfoEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _upsertArrivals(arrivals: List<ArrivalEntry>)

    @Transaction
    @Query("SELECT * FROM state_info_data WHERE id = :stateId")
    abstract fun getStateArrivals(stateId: Int): LiveData<StateArrivals>

    @Transaction
    @Query("SELECT * FROM state_info_data WHERE id = :stateId")
    abstract fun getStateArrivalsSync(stateId: Int): StateArrivals?

    @Query("DELETE FROM arrival_data WHERE DATETIME(arrivalTime) < DATETIME(:currentDateTime)")
    abstract fun _deleteOldArrivals(currentDateTime: LocalDateTime): Int

    @Query("DELETE FROM arrival_data WHERE stateId = :stateId AND mapId NOT IN (:newRequest)")
    abstract fun _deleteArrivalsAtOldStations(stateId: Int, newRequest: List<Int>): Int

    @Query("DELETE FROM arrival_data WHERE stateId = :stateId")
    abstract fun _deleteArrivals(stateId: Int): Int
}