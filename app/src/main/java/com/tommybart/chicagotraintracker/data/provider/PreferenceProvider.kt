package com.tommybart.chicagotraintracker.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

const val USE_DEVICE_LOCATION_PREFERENCE = "USE_DEVICE_LOCATION"
const val DEFAULT_STATION_PREFERENCE = "DEFAULT_STATION"
const val THEME_PREFERENCE = "DARK_THEME"

abstract class PreferenceProvider(context: Context) {
    private val appContext: Context = context.applicationContext
    protected val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)
}