package com.tommybart.chicagotraintracker.data.provider

interface RequestedStationsProvider {
    suspend fun hasLocationRequestChanged(lastLocationMapIds: List<Int>): Boolean
    fun hasDefaultRequestChanged(lastDefaultMapId: Int): Boolean

    suspend fun getNewLocationRequestMapIds(): List<Int>?
    fun getNewDefaultRequestMapId(): Int
}