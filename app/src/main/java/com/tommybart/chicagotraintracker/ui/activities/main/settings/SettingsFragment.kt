package com.tommybart.chicagotraintracker.ui.activities.main.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.models.Station
import com.tommybart.chicagotraintracker.data.provider.DEFAULT_STATION_PREFERENCE
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.ui.activities.main.MainActivity
import com.tommybart.chicagotraintracker.ui.activities.search.SEARCH_ACTIVITY_REQUEST_CODE
import com.tommybart.chicagotraintracker.ui.activities.search.STATION_RESULT_EXTRA

class SettingsFragment : PreferenceFragmentCompat() {

    private var defaultStationPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        defaultStationPref = findPreference(DEFAULT_STATION_PREFERENCE) as Preference?
        defaultStationPref?.setOnPreferenceClickListener { preference ->
            startActivityForResult(preference.intent, SEARCH_ACTIVITY_REQUEST_CODE)
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null) {

            if (data.hasExtra(STATION_RESULT_EXTRA)) {
                val station = data.getSerializableExtra(STATION_RESULT_EXTRA) as Station

                Log.d(TAG, "New default station: ${station.name} | ${station.stationId}")
                // TODO snackbar? how can we get the view from a pref fragment?

                preferenceManager.sharedPreferences
                    .edit()
                    .putInt(DEFAULT_STATION_PREFERENCE, station.stationId)
                    .apply()
            }
        }
    }
}