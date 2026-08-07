package com.example.placeslikee.domain.usecase.profile

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import javax.inject.Inject

class EditMarkerUseCase @Inject constructor(
    private val repository: MapRepository
) {
    suspend operator fun invoke(marker: UIMarker): Result<String> {
        return repository.editMarker(marker)
    }

}