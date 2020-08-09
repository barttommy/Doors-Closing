package com.tommybart.chicagotraintracker.ui.activities.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tommybart.chicagotraintracker.data.repository.StationRepository

class SearchViewModelFactory(
    private val stationRepository: StationRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(stationRepository) as T
    }
}