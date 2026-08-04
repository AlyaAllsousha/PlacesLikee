package com.example.placeslikee.domain.usecase.likes

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.LikeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLikedMarksUseCase @Inject constructor(
    private val repository: LikeRepository
) {
    operator fun invoke(): Flow<List<UIMarker>>{
        return repository.getUsersLikedMarkers()
    }
}