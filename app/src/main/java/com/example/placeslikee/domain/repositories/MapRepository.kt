package com.example.placeslikee.domain.repositories

import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.domain.models.UIMarker
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getMarkers(): Flow<List<UIMarker>>
    suspend fun addMarkers(newMarker: MarkerEntity)
    suspend fun deleteMark(marker: UIMarker)
}