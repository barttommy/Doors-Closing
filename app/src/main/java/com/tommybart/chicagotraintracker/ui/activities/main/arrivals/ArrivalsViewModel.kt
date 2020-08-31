package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider
import com.tommybart.chicagotraintracker.data.repository.RouteRepository
import com.tommybart.chicagotraintracker.internal.Resource
import com.tommybart.chicagotraintracker.internal.arrivalsstate.ArrivalsState
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArrivalsViewModel(
    private val routeRepository: RouteRepository,
    private val preferenceProvider: PreferenceProvider
) : ViewModel() {

    fun isAllowingDeviceLocation(): Boolean = preferenceProvider.isAllowingDeviceLocation()

    private val _arrivalStateLiveData: MutableLiveData<RouteRequest> = MutableLiveData()

    val routeListLiveData: LiveData<Resource<List<Route>>> =
        Transformations.switchMap(_arrivalStateLiveData) { request ->
            routeRepository.getRouteData(
                request.arrivalState,
                request.requestedStationMapIds,
                request.isFetchNeeded
            )
        }

    fun getRouteData(arrivalState: ArrivalsState) {
        CoroutineScope(Dispatchers.IO).launch {
            val requestedStationMapIds = routeRepository.getRequestStationMapIds(arrivalState)
            if (requestedStationMapIds != null) {
                val isFetchNeeded = routeRepository.isFetchRouteDataNeeded(
                    arrivalState,
                    requestedStationMapIds
                )
                val routeRequest = RouteRequest(arrivalState, requestedStationMapIds, isFetchNeeded)
                _arrivalStateLiveData.postValue(routeRequest)
            }
        }
    }

    private data class RouteRequest(
        val arrivalState: ArrivalsState,
        val requestedStationMapIds: List<Int>,
        val isFetchNeeded: Boolean
    )
}
