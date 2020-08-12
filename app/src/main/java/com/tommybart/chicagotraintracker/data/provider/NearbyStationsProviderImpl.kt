package com.tommybart.chicagotraintracker.data.provider

import android.location.Location
import com.tommybart.chicagotraintracker.data.models.Station
import com.tommybart.chicagotraintracker.data.repository.StationRepository
import com.tommybart.chicagotraintracker.internal.TrainLine
import com.tommybart.chicagotraintracker.internal.getDistanceBetweenCoordinates
import java.util.*

private const val LOCATION_REQUEST_RANGE_KM = 1.60934 // One mile

class NearbyStationsProviderImpl(
    private val stationRepository: StationRepository
) : NearbyStationsProvider {

    override suspend fun getNearbyStationMapIds(location: Location): List<Int> {
        var trainLinesInRange = newTrainLinesInRange()
        val stationsInRange = getStationsInRange(location)
        stationsInRange.forEach { station ->
            trainLinesInRange =
                getEnumMapDisjunction(trainLinesInRange, station.availableTrainLines.values)
        }
        val bestStations = requestBestStations(trainLinesInRange, stationsInRange)
        return bestStations.map { station -> station.mapId }
    }

    /*
     * Get all stations within LOCATION_REQUEST_RANGE_KM sorted by the stations closest to the
     * provided location
     */
    private suspend fun getStationsInRange(location: Location): List<Station> {
        val stationList = stationRepository.getStationData()
        val stationsInRangeMap = hashMapOf<Station, Double>()
        stationList.forEach { station ->
            val latitude = station.location.latitude
            val longitude = station.location.longitude
            val distance = getDistanceBetweenCoordinates(
                location.latitude, latitude,
                location.longitude, longitude)
            if (distance <= LOCATION_REQUEST_RANGE_KM) {
                stationsInRangeMap[station] = distance
            }
        }
        return stationsInRangeMap.toList().sortedBy { (_, value) -> value }.toMap().keys.toList()
    }

    /*
     * Returns the closest stations that satisfy the available lines in the area
     */
    private fun requestBestStations(
        trainLinesInRange: EnumMap<TrainLine, Boolean>,
        stationsInRange: List<Station>
    ): List<Station> {
        var currentLinesRepresented = newTrainLinesInRange()
        val bestStations = mutableListOf<Station>()
        stationsInRange.forEach { station ->
            if (currentLinesRepresented == trainLinesInRange) return bestStations
            val newCurrentLinesRepresented =
                getEnumMapDisjunction(currentLinesRepresented, station.availableTrainLines.values)
            if (currentLinesRepresented != newCurrentLinesRepresented) {
                currentLinesRepresented = newCurrentLinesRepresented
                bestStations.add(station)
            }
        }
        return bestStations
    }

    private fun getEnumMapDisjunction(
        enumMap1: EnumMap<TrainLine, Boolean>,
        enumMap2: EnumMap<TrainLine, Boolean>
    ): EnumMap<TrainLine, Boolean> {
        val result = EnumMap<TrainLine, Boolean>(TrainLine::class.java)
        enumMap1.keys.forEach { trainLine ->
            result[trainLine] = enumMap1[trainLine] ?: false || enumMap2[trainLine] ?: false
        }
        return result
    }

    private fun newTrainLinesInRange(): EnumMap<TrainLine, Boolean> =
        TrainLine.values()
            .zip(listOf(false, false, false, false, false, false, false, false, false))
            .toMap(EnumMap(TrainLine::class.java))
}