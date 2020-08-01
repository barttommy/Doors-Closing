package com.tommybart.chicagotraintracker.ui.arrivals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.data.network.cta.CtaApiService
import com.tommybart.chicagotraintracker.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.arrivals_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ArrivalsFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: ArrivalsViewModelFactory by instance<ArrivalsViewModelFactory>()
    private lateinit var viewModel: ArrivalsViewModel

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

        // TODO: Delete, here for testing functionality of api service
        val apiService = CtaApiService(requireContext())
        GlobalScope.launch(Dispatchers.Main) {
            val mapIds = listOf("40530", "41220")
            val response = apiService.getArrivalsAsync(mapIds).await()
            textView_arrivals.text = response.toString()
        }
    }

    private fun bindUI() = launch {
        val stationData = viewModel.stationData.await()
        stationData.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            // TODO
        })
    }
}
