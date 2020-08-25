package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider
import com.tommybart.chicagotraintracker.data.repository.RouteRepository
import com.tommybart.chicagotraintracker.internal.arrivalsstate.ArrivalsState

class ArrivalsViewModel(
    private val routeRepository: RouteRepository,
    preferenceProvider: PreferenceProvider
) : ViewModel() {

    val isAllowingDeviceLocation: Boolean = preferenceProvider.isAllowingDeviceLocation()

    private val arrivalStateLiveData: MutableLiveData<ArrivalsState> = MutableLiveData()

    val routeListLiveData: LiveData<List<Route>?> =
        Transformations.switchMap(arrivalStateLiveData) { state ->
            routeRepository.getRouteData(state)
        }

    fun setArrivalState(arrivalState: ArrivalsState) {
        arrivalStateLiveData.value = arrivalState
    }
}
