package com.tommybart.chicagotraintracker.data.provider

interface RequestedStationsProvider {
    fun getRequestedStationMapIds(): List<Int>
}