package com.tommybart.chicagotraintracker.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

private const val CLARK_LAKE_MAP_ID = 40380

class PreferenceProviderImpl(
    context: Context
) : PreferenceProvider {

    private val appContext: Context = context.applicationContext

    override val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun isAllowingDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION_PREFERENCE, true)
    }

    override fun getDefaultStation(): Int {
        return preferences.getInt(DEFAULT_STATION_PREFERENCE, CLARK_LAKE_MAP_ID)
//        return if (preferences.contains(DEFAULT_STATION_PREFERENCE)){
//            preferences.getInt(DEFAULT_STATION_PREFERENCE, CLARK_LAKE_MAP_ID)
//        } else {
//            null
//        }
    }

    override fun isUsingDarkTheme(): Boolean {
        return preferences.getBoolean(THEME_PREFERENCE, true)
    }
}