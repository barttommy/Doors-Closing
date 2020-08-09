package com.tommybart.chicagotraintracker.data.provider

interface RequestedStationsProvider {
    fun getRequestedStationIds(): List<Int>
}