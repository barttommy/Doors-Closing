package com.tommybart.chicagotraintracker.ui.activities.main.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.models.Station
import com.tommybart.chicagotraintracker.data.provider.DEFAULT_STATION_PREFERENCE
import com.tommybart.chicagotraintracker.data.provider.USE_DARK_THEME_PREFERENCE
import com.tommybart.chicagotraintracker.data.provider.USE_DEVICE_LOCATION_PREFERENCE
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.ui.activities.search.SEARCH_ACTIVITY_REQUEST_CODE
import com.tommybart.chicagotraintracker.ui.activities.search.STATION_RESULT_EXTRA

private const val LOCATION_PERMISSION_REQUEST_CODE = 100

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener,
    Preference.OnPreferenceClickListener {

    private var useLocationPref: SwitchPreference? = null
    private var defaultStationPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        requireActivity().title = "Settings"

        defaultStationPref = findPreference(DEFAULT_STATION_PREFERENCE) as? Preference
        useLocationPref = findPreference(USE_DEVICE_LOCATION_PREFERENCE) as? SwitchPreference

        defaultStationPref?.onPreferenceClickListener = this
        useLocationPref?.onPreferenceChangeListener = this
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun setDefaultStationPref(station: Station) {
        Log.d(TAG, "New default station: ${station.name} | ${station.mapId}")

        preferenceManager.sharedPreferences
            .edit()
            .putInt(DEFAULT_STATION_PREFERENCE, station.mapId)
            .apply()

        val message = "Set ${station.name} as new default station"
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
    }

    private fun setUseLocationPref(useLocation: Boolean) {
        preferenceManager.sharedPreferences
            .edit()
            .putBoolean(USE_DEVICE_LOCATION_PREFERENCE, useLocation)
            .apply()
        useLocationPref?.isChecked = useLocation
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        if (preference != null && preference == defaultStationPref) {
            startActivityForResult(preference.intent, SEARCH_ACTIVITY_REQUEST_CODE)
        }
        return true
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference != null
            && preference is SwitchPreference
            && preference == useLocationPref
            && newValue is Boolean
            && newValue
        ) {
            if (!hasLocationPermission()) {
                requestLocationPermission()
                return false
            }
        }
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            USE_DEVICE_LOCATION_PREFERENCE -> {
                Log.d(TAG, "Use device location changed")
            }
            DEFAULT_STATION_PREFERENCE -> {
                Log.d(TAG, "Default station changed")
            }
            USE_DARK_THEME_PREFERENCE -> {
                Log.d(TAG, "Theme changed")
                activity?.recreate()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            setUseLocationPref(
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            if (data.hasExtra(STATION_RESULT_EXTRA)) {
                val station = data.getSerializableExtra(STATION_RESULT_EXTRA) as? Station
                if (station != null) {
                    setDefaultStationPref(station)
                }
            }
        }
    }
}