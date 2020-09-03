package com.tommybart.chicagotraintracker.data.models

import java.io.Serializable

data class LatLng(
    val latitude: Double,
    val longitude: Double
) : Serializable