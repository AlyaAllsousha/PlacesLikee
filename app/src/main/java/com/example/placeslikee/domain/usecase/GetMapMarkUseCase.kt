package com.example.placeslikee.domain.usecase

import com.example.placeslikee.domain.models.MapPoint
import com.example.placeslikee.domain.repositories.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMapMarkUseCase @Inject constructor(
    val repository: MapRepository
) {
    fun exec(): Flow<List<MapPoint>> {
        return repository.getPoints()
    }
}