package com.example.placeslikee.domain.usecase.markermap

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMapMarkUseCase @Inject constructor(
   private val repository: MapRepository
) {
    operator fun invoke(): Flow<List<UIMarker>> {
        return repository.getMarkers()
    }
}