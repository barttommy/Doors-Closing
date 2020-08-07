package com.tommybart.chicagotraintracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tommybart.chicagotraintracker.data.db.entity.RouteArrivalsInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.RouteEntry
import com.tommybart.chicagotraintracker.data.db.entity.routearrivals.TrainEntry
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.data.db.entity.StationInfoEntry
import com.tommybart.chicagotraintracker.data.db.typeconverters.LocalDateTimeConverter
import com.tommybart.chicagotraintracker.data.db.typeconverters.TrainLineConverter

@Database(
    entities = [
        StationEntry::class,
        StationInfoEntry::class,
        RouteArrivalsInfoEntry::class,
        RouteEntry::class,
        TrainEntry::class
    ],
    version = 1
)
@TypeConverters(TrainLineConverter::class, LocalDateTimeConverter::class)
abstract class DoorsClosingDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao
    abstract fun stationInfoDao(): StationInfoDao
    abstract fun routeArrivalsDao(): RouteArrivalsDao
    abstract fun routeArrivalsInfoDao(): RouteArrivalsInfoDao

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