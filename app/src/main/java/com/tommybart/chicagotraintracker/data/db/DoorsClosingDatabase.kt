package com.tommybart.chicagotraintracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry

@Database(
    entities = [StationEntry::class],
    version = 1
)
abstract class DoorsClosingDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao

    companion object {
        @Volatile private var instance: DoorsClosingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                DoorsClosingDatabase::class.java, "doorsClosing.db")
                .build()
    }
}