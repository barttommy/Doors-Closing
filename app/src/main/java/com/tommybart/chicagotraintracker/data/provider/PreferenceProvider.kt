package com.tommybart.chicagotraintracker.data.provider

import android.content.SharedPreferences

const val SHOULD_REQUEST_LOCATION_PERMISSION = "MAIN_ASKED_FOR_LOCATION"
const val USE_DEVICE_LOCATION_PREFERENCE = "USE_DEVICE_LOCATION"
const val DEFAULT_STATION_PREFERENCE = "DEFAULT_STATION"
const val THEME_PREFERENCE = "DARK_THEME"

interface PreferenceProvider {
    val preferences: SharedPreferences
    fun isAllowingDeviceLocation(): Boolean
    fun getDefaultStation(): Int
    fun isUsingDarkTheme(): Boolean
}