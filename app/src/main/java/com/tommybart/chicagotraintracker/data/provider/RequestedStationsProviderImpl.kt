package com.tommybart.chicagotraintracker.data.provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.tommybart.chicagotraintracker.internal.LocationPermissionNotGrantedException
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.internal.extensions.asDeferred
import kotlinx.coroutines.Deferred

class RequestedStationsProviderImpl(
    context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val nearbyStationsProvider: NearbyStationsProvider,
    private val preferenceProvider: PreferenceProvider
) : RequestedStationsProvider {

    companion object {
        private var simpleLocationCache: Pair<Location, List<Int>>? = null
    }

    private val appContext: Context = context.applicationContext

    override suspend fun hasLocationRequestChanged(lastLocationMapIds: List<Int>): Boolean {
       return try {
            hasLocationRequestMapIdsChanged(lastLocationMapIds)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }
    }

    override fun hasDefaultRequestChanged(lastDefaultMapId: Int): Boolean {
        return hasDefaultPreferenceChanged(lastDefaultMapId)
    }

    override suspend fun getNewLocationRequestMapIds(): List<Int>? {
        try {
            val deviceLocation: Location = getLastDeviceLocationAsync().await()
                ?: return null
            val result = getNearbyStations(deviceLocation)
            return if (result.isEmpty()) null else result
        } catch (e: LocationPermissionNotGrantedException) {
            return null
        }
    }

    override fun getNewDefaultRequestMapId(): Int? {
        return getCustomDefaultStation()
    }

    private suspend fun hasLocationRequestMapIdsChanged(lastRequestedMapIds: List<Int>): Boolean {
        if (!isUsingDeviceLocation()) return false
        val deviceLocation: Location = getLastDeviceLocationAsync().await() ?: return false
        val nearbyStationMapIds = getNearbyStations(deviceLocation)
        return lastRequestedMapIds != nearbyStationMapIds
    }

    private fun getLastDeviceLocationAsync(): Deferred<Location?> {
        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    /*
     * Checks if stations have already been found for location. Returns cached values if they have,
     * and retrieves mapIds from provider if they haven't.
     */
    private suspend fun getNearbyStations(location: Location): List<Int> {
        val cachedMapIds = getCachedLocationMapIds(location)
        if (cachedMapIds != null) return cachedMapIds
        val nearbyStationMapIds = nearbyStationsProvider.getNearbyStationMapIds(location)
        cacheLocationMapIds(location, nearbyStationMapIds)
        return nearbyStationMapIds
    }

    private fun cacheLocationMapIds(location: Location, mapIds: List<Int>) {
        simpleLocationCache = Pair(location, mapIds)
    }

    private fun getCachedLocationMapIds(location: Location): List<Int>? {
        val cachedLocation = simpleLocationCache?.first
        return if (cachedLocation != null
            && location.latitude == cachedLocation.latitude
            && location.longitude == cachedLocation.longitude
        ) {
            Log.d(TAG, "Loading stations from cache")
            simpleLocationCache?.second
        } else {
            Log.d(TAG, "Cannot load from cache for provided location")
            null
        }
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferenceProvider.isAllowingDeviceLocation()
    }

    private fun hasDefaultPreferenceChanged(mapId: Int): Boolean {
        return getCustomDefaultStation() != mapId
    }

    private fun getCustomDefaultStation(): Int? {
        return preferenceProvider.getDefaultStation()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}