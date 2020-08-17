package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Station
import com.tommybart.chicagotraintracker.ui.activities.main.arrivals.arrivalsstate.ArrivalsState
import com.tommybart.chicagotraintracker.ui.activities.main.arrivals.arrivalsstate.ArrivalsStateContext
import com.tommybart.chicagotraintracker.ui.activities.main.arrivals.arrivalsstate.DefaultState
import com.tommybart.chicagotraintracker.ui.activities.main.arrivals.arrivalsstate.SearchState
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.ui.LifecycleBoundLocationManager
import com.tommybart.chicagotraintracker.ui.MarginItemDecoration
import com.tommybart.chicagotraintracker.ui.activities.search.SEARCH_ACTIVITY_REQUEST_CODE
import com.tommybart.chicagotraintracker.ui.activities.search.STATION_RESULT_EXTRA
import com.tommybart.chicagotraintracker.ui.activities.search.SearchActivity
import com.tommybart.chicagotraintracker.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_arrivals.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

// TODO stop location updates for search state
// TODO Implement a way to go back to default state
// TODO snackbar to indicate state switches
class ArrivalsFragment : ScopedFragment(), KodeinAware, View.OnClickListener {

    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: ArrivalsViewModelFactory by instance<ArrivalsViewModelFactory>()
    private lateinit var viewModel: ArrivalsViewModel

    private val fusedLocationProviderClient: FusedLocationProviderClient
        by instance<FusedLocationProviderClient>()
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            refresh(arrivalsStateContext.arrivalsState)
        }
    }
    private var locationManager: LifecycleBoundLocationManager? = null

    private lateinit var arrivalsStateContext: ArrivalsStateContext
    private lateinit var swiper: SwipeRefreshLayout
    private lateinit var arrivalsRecyclerAdapter: ArrivalsRecyclerAdapter
    private val arrivalsRecyclerList = ArrayList<Route>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_arrivals, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ArrivalsViewModel::class.java)
        arrivalsStateContext = ArrivalsStateContext(DefaultState(), viewModel)

        if (viewModel.isAllowingDeviceLocation && hasLocationPermission()) {
            bindLocationManager()
        }
        bindUi()
        refresh(arrivalsStateContext.arrivalsState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_arrivals_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fragment_arrivals_mnu_search -> {
                startActivityForResult(
                    Intent(requireContext(), SearchActivity::class.java),
                    SEARCH_ACTIVITY_REQUEST_CODE
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUi() {
        swiper = fragment_arrivals_swipeLayout
        swiper.setOnRefreshListener { refresh(arrivalsStateContext.arrivalsState) }
        arrivalsRecyclerAdapter = ArrivalsRecyclerAdapter(activity, arrivalsRecyclerList)
        arrivalsRecyclerAdapter.setOnClickListener(this)
        fragment_arrivals_recycler.adapter = arrivalsRecyclerAdapter
        fragment_arrivals_recycler.layoutManager = LinearLayoutManager(activity)
        fragment_arrivals_recycler.addItemDecoration(MarginItemDecoration(24))
    }

    private fun refresh(arrivalsState: ArrivalsState) = launch {
        swiper.isRefreshing = true
        val routeData = arrivalsState.getRouteDataAsync(arrivalsStateContext).await()
        routeData.observe(viewLifecycleOwner, Observer { result ->
            when {
                result == null -> return@Observer
                result.isEmpty() -> {
                    fragment_arrivals_recycler.visibility = View.GONE
                    swiper.isRefreshing = false
                }
                else -> {
                    Log.d(TAG, "Number of routes: ${result.size}")
                    arrivalsRecyclerList.clear()
                    arrivalsRecyclerList.addAll(result)
                    arrivalsRecyclerList.sort()
                    swiper.isRefreshing = false
                    fragment_arrivals_recycler.visibility = View.VISIBLE
                    arrivalsRecyclerAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun bindLocationManager() {
        locationManager = LifecycleBoundLocationManager(
            this,
            fusedLocationProviderClient,
            locationCallback
        )
    }

    // TODO its lifecycle bound so this doesn't guarantee anything
    private fun removeLocationUpdates() {
        //locationManager?.removeLocationUpdates()
    }

    private fun hasLocationPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
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
                    //removeLocationUpdates()
                    refresh(SearchState(station.mapId))
                }
            }
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}