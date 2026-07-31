package com.example.placeslikee.domain.usecase

import com.example.placeslikee.domain.repositories.MapRepository
import javax.inject.Inject

class RefreshMarkersUseCase @Inject constructor(
    private val repository: MapRepository
){
    suspend operator fun invoke() = repository.refresh()
}