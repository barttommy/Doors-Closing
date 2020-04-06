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
class DialogManager (
        private val mainActivity: com.example.chicagotraintracker.activities.MainActivity) {

    fun showErrorDialog(title: Int, message: Int) {
        MaterialDialog(mainActivity).show {
            title(title)
            message(message)
            positiveButton(R.string.OK)
        }
    }
}