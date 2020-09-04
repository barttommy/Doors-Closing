package com.tommybart.chicagotraintracker.ui.activities.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.models.Station
import com.tommybart.chicagotraintracker.data.provider.USE_DARK_THEME_PREFERENCE
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.ui.MarginItemDecoration
import com.tommybart.chicagotraintracker.ui.activities.main.MainActivity
import com.tommybart.chicagotraintracker.ui.base.ScopedActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

const val SEARCH_ACTIVITY_REQUEST_CODE = 123
const val STATION_RESULT_EXTRA = "STATION"

class SearchActivity : ScopedActivity(), KodeinAware, SearchView.OnQueryTextListener,
    View.OnClickListener {

    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: SearchViewModelFactory by instance<SearchViewModelFactory>()
    private lateinit var viewModel: SearchViewModel

    private lateinit var searchRecyclerAdapter: SearchRecyclerAdapter
    private lateinit var stationList: List<Station>
    private val stationSearchResults = ArrayList<Station>()

    public override fun onCreate(savedInstanceState: Bundle?) {

        val isNightMode = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(USE_DARK_THEME_PREFERENCE, true)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(SearchViewModel::class.java)
        bindUi()
    }

    private fun bindUi() {
        searchRecyclerAdapter = SearchRecyclerAdapter(this, stationSearchResults)
        searchRecyclerAdapter.setOnClickListener(this)
        search_activity_recycler.adapter = searchRecyclerAdapter
        search_activity_recycler.layoutManager = LinearLayoutManager(this)
        search_activity_recycler.addItemDecoration(MarginItemDecoration(24))
        title = ""
        launch {
            stationList = viewModel.stationData.await()
            stationSearchResults.addAll(stationList)
            search_activity_grp_refresh.visibility = View.GONE
            searchRecyclerAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_search_menu, menu)
        val searchMenuItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.queryHint = "Search stations"
        searchView.maxWidth = Int.MAX_VALUE
        searchView.isIconifiedByDefault = false
        searchView.requestFocusFromTouch()
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Log.d(TAG, "onQueryTextSubmit: $query")
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (this::stationList.isInitialized) {
            doFilter(newText)
        } else {
            Log.d(TAG, "Cannot search: station data is not ready")
        }
        return false
    }

    @SuppressLint("DefaultLocale")
    private fun doFilter(query: String) {
        stationSearchResults.clear()
        stationList.forEach { station ->
            if (station.name.toLowerCase().contains(query.toLowerCase())) {
                stationSearchResults.add(station)
            }
        }
        stationSearchResults.sort()
        searchRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onClick(view: View) {
        val position = search_activity_recycler.getChildLayoutPosition(view)
        val selected = stationSearchResults[position]
        setResultAndFinish(selected)
    }

    private fun setResultAndFinish(selected: Station) {
        val data = Intent(this, MainActivity::class.java)
        data.putExtra(STATION_RESULT_EXTRA, selected)
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}