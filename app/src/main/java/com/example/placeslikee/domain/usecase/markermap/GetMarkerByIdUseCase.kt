package com.example.placeslikee.domain.usecase.markermap

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMarkerByIdUseCase @Inject constructor(
    private val repository: MarkRepository
) {
    operator fun invoke(markerId: String): Flow<UIMarker?>{
        return repository.getMarkerById(markerId)
    }
}