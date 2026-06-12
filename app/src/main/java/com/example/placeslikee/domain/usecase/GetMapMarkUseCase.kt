package com.example.placeslikee.domain.usecase

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMapMarkUseCase @Inject constructor(
    val repository: MapRepository
) {
    fun exec(): Flow<List<UIMarker>> {
        return repository.getMarkers()
    }
}