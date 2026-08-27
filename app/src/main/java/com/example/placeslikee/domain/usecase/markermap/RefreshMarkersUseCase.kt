package com.example.placeslikee.domain.usecase.markermap

import com.example.placeslikee.domain.repositories.MarkRepository
import javax.inject.Inject

class RefreshMarkersUseCase @Inject constructor(
    private val repository: MarkRepository
){
    suspend operator fun invoke() = repository.refresh()
}