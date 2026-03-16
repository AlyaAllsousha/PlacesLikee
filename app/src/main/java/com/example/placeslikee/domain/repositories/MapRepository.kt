package com.example.placeslikee.domain.repositories

import com.example.placeslikee.domain.models.MapPoint
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getPoints(): Flow<List<MapPoint>>
    suspend fun addPoint(lat: Double, lon: Double, title: String)
}