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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.ui.LifecycleBoundLocationManager
import com.tommybart.chicagotraintracker.ui.MarginItemDecoration
import com.tommybart.chicagotraintracker.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.arrivals_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ArrivalsFragment : ScopedFragment(), KodeinAware, View.OnClickListener {

    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: ArrivalsViewModelFactory by instance<ArrivalsViewModelFactory>()
    private lateinit var viewModel: ArrivalsViewModel

    private val fusedLocationProviderClient: FusedLocationProviderClient
        by instance<FusedLocationProviderClient>()
    private val locationCallback: LocationCallback = object : LocationCallback() { }

    private lateinit var swiper: SwipeRefreshLayout
    private lateinit var arrivalsRecyclerAdapter: ArrivalsRecyclerAdapter
    private val arrivalsRecyclerList = ArrayList<Route>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.arrivals_fragment, container, false)
    }

    // TODO can we make the location callback setup a refresh as well?
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ArrivalsViewModel::class.java)

        if (hasLocationPermission()) {
            bindLocationManager()
        }
        bindUi()
        refresh()
    }

    private fun bindUi() {
        swiper = arrivals_fragment_swipeLayout
        swiper.setOnRefreshListener { refresh() }
        arrivalsRecyclerAdapter = ArrivalsRecyclerAdapter(activity, arrivalsRecyclerList)
        arrivalsRecyclerAdapter.setOnClickListener(this)
        arrivals_fragment_recycler.adapter = arrivalsRecyclerAdapter
        arrivals_fragment_recycler.layoutManager = LinearLayoutManager(activity)
        arrivals_fragment_recycler.addItemDecoration(MarginItemDecoration(24))
    }

    private fun refresh() = launch {
        swiper.isRefreshing = true
        // TODO should we only observe here? not await? or is it the other way around
        val routeData = viewModel.getRouteDataAsync().await()
        routeData.observe(viewLifecycleOwner, Observer { result ->
            swiper.isRefreshing = false
            when {
                result == null -> return@Observer
                result.isEmpty() -> arrivals_fragment_recycler.visibility = View.GONE
                else -> {
                    Log.d(TAG, "Number of routes: ${result.size}")
                    arrivals_fragment_recycler.visibility = View.VISIBLE
                    arrivalsRecyclerList.clear()
                    arrivalsRecyclerList.addAll(result)
                    arrivalsRecyclerList.sort()
                    arrivalsRecyclerAdapter.notifyDataSetChanged()
                }
            }
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

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}
