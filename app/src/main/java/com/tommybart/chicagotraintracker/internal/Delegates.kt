package com.tommybart.chicagotraintracker.internal

import kotlinx.coroutines.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun <T> lazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {
    return lazy {
        GlobalScope.async(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
    }
}

// Haversine formula to calculate distance between coordinates
fun getDistanceBetweenCoordinates(lat1: Double, lat2: Double, lon1: Double, lon2: Double): Double {
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
        + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
        * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val earthRadiusKm = 6371.0
    return earthRadiusKm * c
}