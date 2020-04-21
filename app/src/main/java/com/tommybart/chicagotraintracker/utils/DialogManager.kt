package com.tommybart.chicagotraintracker.utils

import com.afollestad.materialdialogs.MaterialDialog
import com.tommybart.chicagotraintracker.R

/*
 * Created using MaterialDialogs
 * See documentation here: https://github.com/afollestad/material-dialogs
 */
class DialogManager (
        private val mainActivity: com.tommybart.chicagotraintracker.activities.MainActivity) {

    fun showErrorDialog(title: Int, message: Int) {
        MaterialDialog(mainActivity).show {
            title(title)
            message(message)
            positiveButton(R.string.OK)
        }
    }
}