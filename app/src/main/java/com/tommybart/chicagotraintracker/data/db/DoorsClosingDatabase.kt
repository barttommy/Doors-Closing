package com.tommybart.chicagotraintracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tommybart.chicagotraintracker.data.db.entity.*

@Database(
    entities = [StationEntry::class, RouteEntry::class, TrainEntry::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class DoorsClosingDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao
    abstract fun routeDao(): RouteDao

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