package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.tommybart.chicagotraintracker.internal.ArrivalState
import com.tommybart.chicagotraintracker.internal.Resource
import com.tommybart.chicagotraintracker.ui.LifecycleBoundLocationManager
import com.tommybart.chicagotraintracker.ui.MarginItemDecoration
import com.tommybart.chicagotraintracker.ui.activities.search.SEARCH_ACTIVITY_REQUEST_CODE
import com.tommybart.chicagotraintracker.ui.activities.search.STATION_RESULT_EXTRA
import com.tommybart.chicagotraintracker.ui.activities.search.SearchActivity
import com.tommybart.chicagotraintracker.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_arrivals.*
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
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            if (!swiper.isRefreshing) refresh()
        }
    }
    private var locationManager: LifecycleBoundLocationManager? = null

    private var searchMenuItem: MenuItem? = null
    private var returnToDefaultMenuItem: MenuItem? = null

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

        // Only restore search state. See ArrivalsViewModel.kt
        val arrivalsState = if (viewModel.isStateInitialized()
            && viewModel.state is ArrivalState.Search
        ) {
            viewModel.state
        } else if (viewModel.isAllowingDeviceLocation() && hasLocationPermission()) {
            ArrivalState.Location()
        } else {
            ArrivalState.Default()
        }

        updateState(arrivalsState)
        bindUi()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            if (data.hasExtra(STATION_RESULT_EXTRA)) {
                val station = data.getSerializableExtra(STATION_RESULT_EXTRA) as? Station
                station?.let { updateState(ArrivalState.Search(it.mapId)) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isAllowingDeviceLocation()
            && hasLocationPermission()
            && viewModel.state is ArrivalState.Default
        ) {
            // Case: State transition when user accepts location permission on first run
            updateState(ArrivalState.Location())
            refresh()
        } else if (viewModel.state is ArrivalState.Location && !hasLocationPermission()) {
            // Case: Location permission turned off while in location state
            viewModel.useDeviceLocation(false)
            updateState(ArrivalState.Default())
            refresh()
        } else if (!swiper.isRefreshing) {
            // Case: Everything is normal, refresh if we aren't already
            refresh()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_arrivals_menu, menu)
        searchMenuItem = menu.findItem(R.id.fragment_arrivals_mnu_search)
        returnToDefaultMenuItem = menu.findItem(R.id.fragment_arrivals_mnu_returnFromSearchState)
        updateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fragment_arrivals_mnu_search -> {
                startActivityForResult(
                    Intent(requireContext(), SearchActivity::class.java),
                    SEARCH_ACTIVITY_REQUEST_CODE
                )
            }
            R.id.fragment_arrivals_mnu_returnFromSearchState -> {
                if (viewModel.isAllowingDeviceLocation() && hasLocationPermission()) {
                    updateState(ArrivalState.Location())
                } else {
                    updateState(ArrivalState.Default())
                }
                refresh()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUi() {
        swiper = fragment_arrivals_swipeLayout
        swiper.setOnRefreshListener { refresh() }
        arrivalsRecyclerAdapter = ArrivalsRecyclerAdapter(activity, arrivalsRecyclerList)
        arrivalsRecyclerAdapter.setOnClickListener(this)
        fragment_arrivals_recycler.adapter = arrivalsRecyclerAdapter
        fragment_arrivals_recycler.layoutManager = LinearLayoutManager(activity)
        fragment_arrivals_recycler.addItemDecoration(MarginItemDecoration(24))
        viewModel.routeListLiveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Loading -> return@Observer
                is Resource.Error, null -> {
                    swiper.isRefreshing = false
                    fragment_arrivals_recycler.visibility = View.GONE
                }
                is Resource.Success -> {
                    if (result.data == null || result.data.isEmpty()) {
                        fragment_arrivals_recycler.visibility = View.GONE
                    } else {
                        updateRecycler(result.data)
                        fragment_arrivals_recycler.visibility = View.VISIBLE
                    }
                    swiper.isRefreshing = false
                }
            }
        })
    }

    private fun refresh() {
        viewModel.getRouteData()
        swiper.isRefreshing = true
    }

    private fun updateState(arrivalsState: ArrivalState) {
        viewModel.updateState(arrivalsState)
        updateLocationUsage()
        updateTitle()
        updateOptionsMenu()
    }

    private fun updateLocationUsage() {
        when (viewModel.state) {
            is ArrivalState.Location -> getLocationUpdates()
            else -> removeLocationUpdates()
        }
    }

    private fun updateTitle() {
        requireActivity().title = viewModel.state.title
    }

    private fun updateOptionsMenu() {
        when (viewModel.state) {
            is ArrivalState.Search -> {
                searchMenuItem?.isVisible = false
                returnToDefaultMenuItem?.isVisible = true
            }
            else -> {
                searchMenuItem?.isVisible = true
                returnToDefaultMenuItem?.isVisible = false
            }
        }
    }

    private fun updateRecycler(result: List<Route>) {
        result.map { route -> route.arrivals.sort() }
        arrivalsRecyclerList.clear()
        arrivalsRecyclerList.addAll(result)
        arrivalsRecyclerList.sort()

        arrivalsRecyclerAdapter.notifyDataSetChanged()
    }

    private fun getLocationUpdates() {
        if (locationManager == null)
            bindLocationManager()
        if (locationManager?.isEnabled == false)
            restartLocationUpdates()
    }

    private fun bindLocationManager() {
        locationManager = LifecycleBoundLocationManager(
            this,
            fusedLocationProviderClient,
            locationCallback,
            true
        )
    }

    private fun restartLocationUpdates() {
        locationManager?.isEnabled = true
        locationManager?.startLocationUpdates()
    }

    private fun removeLocationUpdates() {
        locationManager?.isEnabled = false
        locationManager?.removeLocationUpdates()
    }

    private fun hasLocationPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}