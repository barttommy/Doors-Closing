package com.tommybart.chicagotraintracker.ui.activities.main

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.provider.MAIN_SHOULD_REQUEST_LOCATION_PERMISSION
import com.tommybart.chicagotraintracker.data.provider.THEME_PREFERENCE
import com.tommybart.chicagotraintracker.data.provider.USE_DEVICE_LOCATION_PREFERENCE
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

private const val LOCATION_PERMISSION_REQUEST_CODE = 100

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isNightMode = sharedPreferences.getBoolean(THEME_PREFERENCE, true)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_arrivals, R.id.nav_about, R.id.nav_settings),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Only ask for location on first install. Permission can be requested again in the settings
        // fragment.
        if (sharedPreferences.getBoolean(MAIN_SHOULD_REQUEST_LOCATION_PERMISSION, true)) {
            requestLocationPermission()
        }
        sharedPreferences.edit()
            .putBoolean(MAIN_SHOULD_REQUEST_LOCATION_PERMISSION, false)
            .apply()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
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
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                sharedPreferences.edit()
                    .putBoolean(USE_DEVICE_LOCATION_PREFERENCE, false)
                    .apply()
            }
        }
    }
}
