package com.tommybart.chicagotraintracker.ui.arrivals

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.network.ChicagoDataPortalApiService
import kotlinx.android.synthetic.main.arrivals_fragment.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArrivalsFragment : Fragment() {

    companion object {
        fun newInstance() = ArrivalsFragment()
    }

    private lateinit var viewModel: ArrivalsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.arrivals_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ArrivalsViewModel::class.java)
        // TODO: Use the ViewModel

        // TODO: DELETE (here for now to ensure functionality)
        val api = ChicagoDataPortalApiService(requireContext())
        GlobalScope.launch(Dispatchers.Main) {
            val stationDataResponse = api.getStationDataAsync().await()
            textView_arrivals.text = stationDataResponse[0].toString()
        }
    }
}
