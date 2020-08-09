package com.tommybart.chicagotraintracker.data.models

import java.io.Serializable

data class Location(
    val latitude: Double,
    val longitude: Double
) : Serializable