package com.tommybart.chicagotraintracker.ui.arrivals

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import com.tommybart.chicagotraintracker.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.arrivals_fragment.*
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
        bindUI()
    }

    private fun bindUI() = launch {
//        val stationData = viewModel.stationData.await()
//        stationData.observe(viewLifecycleOwner, Observer {
//            if (it == null) return@Observer
//        })

        val routeData = viewModel.routeData.await()
        routeData.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            textView_arrivals.text = it.toString()
        })
    }
}
