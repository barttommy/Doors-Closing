package com.tommybart.chicagotraintracker.ui.activities.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider

class MainViewModelFactory(
    private val preferenceProvider: PreferenceProvider
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(preferenceProvider) as T
    }
}