package com.example.placeslikee.domain.repositories

import com.example.placeslikee.domain.models.UIMarker
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getMarkers(): Flow<List<UIMarker>>
    suspend fun addMarkers(newMarker: UIMarker)
    suspend fun updateMarker(marker: UIMarker)
    suspend fun deleteMark(marker: UIMarker)
    suspend fun refreshMarkers()
}