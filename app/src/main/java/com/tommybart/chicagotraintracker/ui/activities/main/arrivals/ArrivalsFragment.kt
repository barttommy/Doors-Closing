package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.ui.LifecycleBoundLocationManager
import com.tommybart.chicagotraintracker.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.arrivals_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ArrivalsFragment : ScopedFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: ArrivalsViewModelFactory by instance<ArrivalsViewModelFactory>()
    private lateinit var viewModel: ArrivalsViewModel

    private val fusedLocationProviderClient: FusedLocationProviderClient
        by instance<FusedLocationProviderClient>()
    private val locationCallback: LocationCallback = object : LocationCallback() { }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.arrivals_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ArrivalsViewModel::class.java)
        bindUI()
        if (hasLocationPermission()) {
            bindLocationManager()
        }
    }

    private fun bindUI() = launch {
        val routeData = viewModel.routeData.await()
        routeData.observe(viewLifecycleOwner, Observer { routeList ->
            if (routeList == null) return@Observer
            Log.d(TAG, "Number of routes: ${routeList.size}")
            textView_arrivals.text = routeList.toString()
        })
    }

    private fun bindLocationManager() {
        LifecycleBoundLocationManager(
            this,
            fusedLocationProviderClient,
            locationCallback
        )
    }

    private fun hasLocationPermission(): Boolean {
        return viewModel.isAllowingDeviceLocation &&
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }
}
