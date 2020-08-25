package com.tommybart.chicagotraintracker.data.provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.tommybart.chicagotraintracker.internal.LocationPermissionNotGrantedException
import com.tommybart.chicagotraintracker.internal.extensions.asDeferred
import kotlinx.coroutines.Deferred

class RequestedStationsProviderImpl(
    context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val nearbyStationsProvider: NearbyStationsProvider,
    private val preferenceProvider: PreferenceProvider
) : RequestedStationsProvider {

    private val appContext: Context = context.applicationContext

    override suspend fun hasLocationRequestChanged(lastLocationMapIds: List<Int>): Boolean {
       return try {
            hasNearbyStationsChanged(lastLocationMapIds)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }
    }

    override fun hasDefaultRequestChanged(lastDefaultMapId: Int): Boolean {
        return hasDefaultPreferenceChanged(lastDefaultMapId)
    }

    // TODO: can we avoid doing things twice between this and hasRequestedStationsChanged?
    override suspend fun getNewLocationRequestMapIds(): List<Int>? {
        try {
            val deviceLocation: Location = getLastDeviceLocationAsync().await()
                ?: return null
            val result = nearbyStationsProvider.getNearbyStationMapIds(deviceLocation)
            return if (result.isEmpty()) null else result
        } catch (e: LocationPermissionNotGrantedException) {
            return null
        }
    }

    override fun getNewDefaultRequestMapId(): Int {
        return getCustomDefaultStation()
    }

    private suspend fun hasNearbyStationsChanged(lastRequestedMapIds: List<Int>): Boolean {
        if (!isUsingDeviceLocation()) return false
        val deviceLocation: Location = getLastDeviceLocationAsync().await() ?: return false
        return lastRequestedMapIds != nearbyStationsProvider.getNearbyStationMapIds(
            deviceLocation
        )
    }

    private fun getLastDeviceLocationAsync(): Deferred<Location?> {
        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferenceProvider.isAllowingDeviceLocation()
    }

    private fun hasDefaultPreferenceChanged(mapId: Int): Boolean {
        return getCustomDefaultStation() != mapId
    }

    private fun getCustomDefaultStation(): Int {
        return preferenceProvider.getDefaultStation()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}