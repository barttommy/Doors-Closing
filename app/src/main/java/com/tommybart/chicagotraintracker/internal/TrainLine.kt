package com.tommybart.chicagotraintracker.internal

import android.annotation.SuppressLint
import android.content.res.Resources
import com.tommybart.chicagotraintracker.R

enum class TrainLine(val value: String) {
    RED("red"),
    BLUE("blue"),
    BROWN("brn"),
    GREEN("g"),
    ORANGE("o"),
    PINK("pnk"),
    PURPLE("p"),
    PURPLE_EXPRESS("pexp"),
    YELLOW("y");

    companion object {
        private val values : Array<TrainLine> = values()

        @SuppressLint("DefaultLocale")
        fun fromValue(value: String) = values.firstOrNull { it.value == value.toLowerCase() }
    }

    fun getColor(resources: Resources): Int {
        return when(this) {
            RED -> resources.getColor(R.color.redLine, null)
            BLUE -> resources.getColor(R.color.blueLine, null)
            BROWN -> resources.getColor(R.color.brownLine, null)
            GREEN -> resources.getColor(R.color.greenLine, null)
            ORANGE -> resources.getColor(R.color.orangeLine, null)
            PINK -> resources.getColor(R.color.pinkLine, null)
            PURPLE -> resources.getColor(R.color.purpleLine, null)
            PURPLE_EXPRESS -> resources.getColor(R.color.purpleLine, null)
            YELLOW -> resources.getColor(R.color.yellowLine, null)
        }
    }
}