package com.example.placeslikee.domain.usecase

import com.example.placeslikee.domain.repositories.MapRepository
import javax.inject.Inject

class CreateMarkerUseCase @Inject constructor(
    private val repositoryImpl: MapRepository
) {
    operator fun invoke(){

    }
}