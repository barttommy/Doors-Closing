package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider
import com.tommybart.chicagotraintracker.data.provider.RequestedStationsProvider
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository
import com.tommybart.chicagotraintracker.internal.lazyDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ArrivalsViewModel(
    private val routeArrivalsRepository: RouteArrivalsRepository,
    preferenceProvider: PreferenceProvider
) : ViewModel() {

    val isAllowingDeviceLocation: Boolean = preferenceProvider.isAllowingDeviceLocation()

//    val routeData: Deferred<LiveData<List<Route>>> by lazyDeferred {
//        routeArrivalsRepository.getRouteData()
//    }

    suspend fun getRouteDataAsync(): Deferred<LiveData<List<Route>>> =
        GlobalScope.async {
            routeArrivalsRepository.getRouteData()
        }
}
