package com.tommybart.chicagotraintracker.data.network.chicagodataportal.response

import com.tommybart.chicagotraintracker.data.db.entity.StationEntry

class SodaApiResponse(
    val stationEntries: List<StationEntry>
)