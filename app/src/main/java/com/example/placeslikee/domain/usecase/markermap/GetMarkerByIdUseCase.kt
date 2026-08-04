package com.example.placeslikee.domain.usecase.markermap

import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMarkerByIdUseCase @Inject constructor(
    private val repository: MapRepository
) {
    operator fun invoke(markerId: String): Flow<UIMarker?>{
        return repository.getMarkerById(markerId)
    }
}