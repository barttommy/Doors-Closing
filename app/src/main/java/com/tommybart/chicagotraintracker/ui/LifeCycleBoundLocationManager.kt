package com.tommybart.chicagotraintracker.ui

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.tommybart.chicagotraintracker.internal.extensions.TAG

private const val LOCATION_MIN_TIME: Long = 15 * 1000
const val LOCATION_MIN_DIST_METERS: Float = 100f // Chicago blocks are typically 200m x 100m

class LifecycleBoundLocationManager(
    lifecycleOwner: LifecycleOwner,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationCallback: LocationCallback,
    var isEnabled: Boolean
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val locationRequest: LocationRequest = LocationRequest().apply {
        interval = LOCATION_MIN_TIME
        fastestInterval = LOCATION_MIN_TIME
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = LOCATION_MIN_DIST_METERS
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startLocationUpdates() {
        if (isEnabled) {
            Log.d(TAG, "Starting location updates")
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeLocationUpdates() {
        Log.d(TAG, "Removing location updates")
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}