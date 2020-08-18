package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ArrivalsViewModel(
    private val routeArrivalsRepository: RouteArrivalsRepository,
    preferenceProvider: PreferenceProvider
) : ViewModel() {

    val isAllowingDeviceLocation: Boolean = preferenceProvider.isAllowingDeviceLocation()

    suspend fun getRouteDataAsync(): Deferred<LiveData<List<Route>>> =
        GlobalScope.async {
            routeArrivalsRepository.getRouteData()
        }

    suspend fun getRouteDataSearchAsync(searchMapId: Int): Deferred<LiveData<List<Route>>> =
        GlobalScope.async {
            routeArrivalsRepository.getRouteDataSearch(searchMapId)
        }
}
