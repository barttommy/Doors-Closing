package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import com.tommybart.chicagotraintracker.data.db.entity.StationEntry

class SodaApiResponse(
    val stationEntries: List<StationEntry>
)