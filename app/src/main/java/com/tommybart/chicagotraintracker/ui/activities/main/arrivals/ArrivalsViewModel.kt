package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider
import com.tommybart.chicagotraintracker.data.provider.USE_DEVICE_LOCATION_PREFERENCE
import com.tommybart.chicagotraintracker.data.repository.RouteRepository
import com.tommybart.chicagotraintracker.internal.ArrivalState
import com.tommybart.chicagotraintracker.internal.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArrivalsViewModel(
    private val routeRepository: RouteRepository,
    private val preferenceProvider: PreferenceProvider
) : ViewModel() {

    companion object {
        const val STATE_ID = "STATE_ID"
        const val SEARCH_MAP_ID = "SEARCH_STATION_ID"
    }

    lateinit var state: ArrivalState
    private val preferences: SharedPreferences = preferenceProvider.preferences

    private val _arrivalStateLiveData: MutableLiveData<RouteRequest?> = MutableLiveData()

    val routeListLiveData: LiveData<Resource<List<Route>>> =
        Transformations.switchMap(_arrivalStateLiveData) { request ->
            if (request == null) {
                val error = MutableLiveData<Resource<List<Route>>>()
                error.postValue(Resource.Error("Can't get requested station map ids"))
                error
            } else {
                routeRepository.getRouteData(
                    request.arrivalState,
                    request.requestedStationMapIds,
                    request.isFetchNeeded
                )
            }
        }

    init {
        loadState()
    }

    fun getRouteData() {
        CoroutineScope(Dispatchers.IO).launch {
            val requestedStationMapIds = routeRepository.getRequestStationMapIds(state)
            if (requestedStationMapIds != null) {
                val isFetchNeeded = routeRepository.isFetchRouteDataNeeded(
                    state,
                    requestedStationMapIds
                )
                val routeRequest = RouteRequest(state, requestedStationMapIds, isFetchNeeded)
                _arrivalStateLiveData.postValue(routeRequest)
            } else {
                _arrivalStateLiveData.postValue(null)
            }
        }
    }

    fun isAllowingDeviceLocation(): Boolean = preferenceProvider.isAllowingDeviceLocation()

    fun useDeviceLocation(useLocation: Boolean) {
        preferences.edit()
            .putBoolean(USE_DEVICE_LOCATION_PREFERENCE, useLocation)
            .apply()
    }

    fun isStateInitialized(): Boolean = this::state.isInitialized

    // Returns true if state changed
    fun updateState(arrivalsState: ArrivalState): Boolean {
        if (isStateInitialized() && state == arrivalsState) return false
        state = arrivalsState
        saveState()
        return true
    }

    // Only restore search state - other states determined by settings that could have changed.
    private fun loadState() {
        if (preferences.contains(STATE_ID)) {
            val stateId = preferences.getInt(STATE_ID, -1)
            val mapId = preferences.getInt(SEARCH_MAP_ID, -1)
            if (stateId == ArrivalState.SEARCH_STATE_ID && mapId != -1) {
                state = ArrivalState.Search(mapId)
            }
        }
    }

    private fun saveState() {
        val mapId = (state as? ArrivalState.Search)?.mapId
        if (mapId != null) {
            preferences.edit()
                .putInt(STATE_ID, state.id)
                .putInt(SEARCH_MAP_ID, mapId)
                .apply()
        } else {
            preferences.edit()
                .putInt(STATE_ID, state.id)
                .apply()
        }
    }

    private data class RouteRequest(
        val arrivalState: ArrivalState,
        val requestedStationMapIds: List<Int>,
        val isFetchNeeded: Boolean
    )
}
