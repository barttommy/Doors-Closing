package com.tommybart.chicagotraintracker.internal

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.provider.USE_DARK_THEME_PREFERENCE

/*
 * Note: purple express and purple lines both appear as "p" in the CTA api response. Pexp is needed
 * for the CDP api and Customer Alerts api (unimplemented). The "value" of each enum (except for
 * purple express) is how they appear in the CTA api response, which slightly differs from the CDP
 * response (pink and orange line representations are mildly different).
 */
enum class TrainLine(val value: String) {
    RED("red"),
    BLUE("blue"),
    BROWN("brn"),
    GREEN("g"),
    ORANGE("org"),
    PINK("pink"),
    PURPLE("p"),
    PURPLE_EXPRESS("pexp"),
    YELLOW("y");

    companion object {
        private val values: Array<TrainLine> = values()

        @SuppressLint("DefaultLocale")
        fun fromValue(value: String) = values.firstOrNull { it.value == value.toLowerCase() }
    }

    fun getColor(context: Context): Int {
        return when (this) {
            RED -> ContextCompat.getColor(context, R.color.redLine)
            BLUE -> ContextCompat.getColor(context, R.color.blueLine)
            BROWN -> ContextCompat.getColor(context, R.color.brownLine)
            GREEN -> ContextCompat.getColor(context, R.color.greenLine)
            ORANGE -> ContextCompat.getColor(context, R.color.orangeLine)
            PINK -> ContextCompat.getColor(context, R.color.pinkLine)
            PURPLE -> ContextCompat.getColor(context, R.color.purpleLine)
            PURPLE_EXPRESS -> ContextCompat.getColor(context, R.color.purpleLine)
            YELLOW -> {
                val usingDarkTheme = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(USE_DARK_THEME_PREFERENCE, true)
                if (usingDarkTheme) ContextCompat.getColor(context, R.color.yellowLine)
                else ContextCompat.getColor(context, R.color.yellowLineAlt)
            }
        }
    }
}