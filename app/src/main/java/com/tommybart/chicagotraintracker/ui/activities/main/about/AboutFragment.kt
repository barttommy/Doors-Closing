package com.tommybart.chicagotraintracker.ui.activities.main.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.tommybart.chicagotraintracker.BuildConfig
import com.tommybart.chicagotraintracker.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUi()
    }

    private fun bindUi() {
        requireActivity().title = "About"
        about_tv_versionNumber.text = resources.getString(
            R.string.about_version_text, BuildConfig.VERSION_NAME
        )
    }

}
