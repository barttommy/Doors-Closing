package com.tommybart.chicagotraintracker.internal.extensions

val Any.TAG: String
    get() {
        val tag = "TAG/$javaClass.simpleName"
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }