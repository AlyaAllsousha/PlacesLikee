package com.example.placeslikee.domain.repositories

import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.domain.models.UIMarker
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getMarkers(): Flow<List<UIMarker>>
    suspend fun addMarkers(newMarker: MarkerEntity):Result<Unit>
    suspend fun deleteMark(id: String):Result<Unit>
    fun getMarkerById(markerId: String): Flow<UIMarker?>
    suspend fun editMarker(marker: UIMarker): Result<String>
    fun getMarkersByUserId(userId: String):Flow<List<UIMarker>>
    suspend fun refresh()
}