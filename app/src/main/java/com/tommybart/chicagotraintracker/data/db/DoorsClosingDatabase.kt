package com.tommybart.chicagotraintracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.GsonBuilder
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.data.db.entity.StationInfoEntry
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.ArrivalEntry
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.StateInfoEntry
import com.tommybart.chicagotraintracker.data.db.typeconverters.IntListConverter
import com.tommybart.chicagotraintracker.data.db.typeconverters.LocalDateConverter
import com.tommybart.chicagotraintracker.data.db.typeconverters.LocalDateTimeConverter
import com.tommybart.chicagotraintracker.data.db.typeconverters.TrainLineConverter
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.SodaResponseDeserializer
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.response.SodaApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

@Database(
    entities = [
        StationEntry::class,
        StationInfoEntry::class,
        StateInfoEntry::class,
        ArrivalEntry::class
    ],
    version = 1
)
@TypeConverters(
    TrainLineConverter::class,
    LocalDateConverter::class,
    LocalDateTimeConverter::class,
    IntListConverter::class
)
abstract class DoorsClosingDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao
    abstract fun stationInfoDao(): StationInfoDao
    abstract fun stateArrivalsDao(): StateArrivalsDao

    companion object {
        @Volatile
        private var instance: DoorsClosingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DoorsClosingDatabase::class.java,
                "doorsClosing.db"
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateStationData(context)
                    }
                }
            }).build()

        /*
         * Pre-populates station data by parsing a SodaApiResponse (fetched on PREPOPULATE_DATA_DATE)
         * stored in the assets folder. The app then checks if updates are needed to this data (once
         * a day) since changes to this data set are relatively rare.
         */
        private fun prepopulateStationData(context: Context) {
            val reader = context.assets
                .open("station_prepopulate_data.json")
                .bufferedReader()
            val gson = GsonBuilder()
                .registerTypeAdapter(SodaApiResponse::class.java, SodaResponseDeserializer())
                .create()
            val response = gson.fromJson(reader, SodaApiResponse::class.java)

            instance?.stationDao()?.insertAll(response.stationEntries)
            instance?.stationInfoDao()
                ?.upsert(StationInfoEntry(PREPOPULATE_DATA_DATE, PREPOPULATE_DATA_DATE))
        }

        // The date prepopulate data was downloaded to check against for updates
        private val PREPOPULATE_DATA_DATE: LocalDate = LocalDate.parse("2020-08-29")
    }
}