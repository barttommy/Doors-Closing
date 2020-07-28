package com.tommybart.chicagotraintracker.utils

import com.afollestad.materialdialogs.MaterialDialog
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.activities.ArrivalsActivity

/*
 * Created using MaterialDialogs
 * See documentation here: https://github.com/afollestad/material-dialogs
 */
class DialogManager (
        private val arrivalsActivity: ArrivalsActivity) {

    fun showErrorDialog(title: Int, message: Int) {
        MaterialDialog(arrivalsActivity).show {
            title(title)
            message(message)
            positiveButton(R.string.OK)
        }
    }
}