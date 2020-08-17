package com.tommybart.chicagotraintracker.ui.activities.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider

class MainViewModel(
    preferenceProvider: PreferenceProvider
): ViewModel() {
    val preferences: SharedPreferences = preferenceProvider.preferences
}