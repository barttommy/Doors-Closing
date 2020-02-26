package com.example.chicagotraintracker.utils

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.example.chicagotraintracker.R
import com.example.chicagotraintracker.models.Station

/*
 * Created using MaterialDialogs
 * See documentation here: https://github.com/afollestad/material-dialogs
 */
class DialogManager (private val mainActivity: com.example.chicagotraintracker.activities.MainActivity) {

    fun showInputDialog() {
        MaterialDialog(mainActivity).show {
            title(R.string.search_title)
            input(hintRes = R.string.search_hint, waitForPositiveButton = true) { _, text ->
                val searchResult = mainActivity.search(text.toString().trim())
                when {
                    searchResult.isEmpty() -> {
                        showErrorDialog(R.string.error_search_title, R.string.error_search_message)
                    }
                    searchResult.size == 1 -> {
                        mainActivity.loadManualRequest(searchResult[0])
                    }
                    else -> {
                        showResultsDialog(searchResult)
                    }
                }
            }
            positiveButton(R.string.search)
        }
    }

    private fun showResultsDialog(stations: ArrayList<Station>) {
        val myItems = arrayListOf<String>()
        for (i in stations.indices) {
            myItems.add(stations.get(i).detailedName)
        }
        MaterialDialog(mainActivity).show {
            title(R.string.search_results_title)
            listItems(items = myItems.toList()) { _, index, _ ->
                mainActivity.loadManualRequest(stations.get(index))
            }
        }
    }

    fun showErrorDialog(title: Int, message: Int) {
        MaterialDialog(mainActivity).show {
            title(title)
            message(message)
            positiveButton(R.string.OK)
        }
    }
}