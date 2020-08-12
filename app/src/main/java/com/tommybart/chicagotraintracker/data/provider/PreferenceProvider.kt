package com.tommybart.chicagotraintracker.data.provider

const val USE_DEVICE_LOCATION_PREFERENCE = "USE_DEVICE_LOCATION"
const val DEFAULT_STATION_PREFERENCE = "DEFAULT_STATION"
const val THEME_PREFERENCE = "DARK_THEME"

interface PreferenceProvider {
    fun isAllowingDeviceLocation(): Boolean
    fun getDefaultStation(): Int
    fun isUsingDarkTheme(): Boolean
}