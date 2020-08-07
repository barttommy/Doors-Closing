package com.tommybart.chicagotraintracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommybart.chicagotraintracker.data.db.entity.responseinfo.RESPONSE_INFO_ID
import com.tommybart.chicagotraintracker.data.db.entity.responseinfo.ResponseInfoEntry

@Dao
interface ResponseInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(responseInfoEntry: ResponseInfoEntry)

    @Query("SELECT * FROM response_info WHERE id = $RESPONSE_INFO_ID")
    fun getResponseInfo(): LiveData<ResponseInfoEntry>

    @Query("SELECT * FROM response_info WHERE id = $RESPONSE_INFO_ID")
    fun getResponseInfoSync(): ResponseInfoEntry?
}