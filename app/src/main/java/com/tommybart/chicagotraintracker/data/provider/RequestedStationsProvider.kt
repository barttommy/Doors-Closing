package com.tommybart.chicagotraintracker.data.provider

interface RequestedStationsProvider {
    suspend fun hasRequestedStationsChanged(requestedStationMapIds: List<Int>): Boolean
    suspend fun getNewRequestMapIds(): List<Int>?
}