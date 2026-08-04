package com.example.placeslikee.domain.repositories

import com.example.placeslikee.data.local.entities.LikesEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.domain.models.UIMarker
import kotlinx.coroutines.flow.Flow

interface LikeRepository {
    suspend fun toggleLikeToMarker(markerId: String)
    fun getUsersLikedMarkers(): Flow<List<UIMarker>>
}