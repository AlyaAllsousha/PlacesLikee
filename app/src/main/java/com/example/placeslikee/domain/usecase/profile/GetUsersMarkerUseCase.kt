package com.example.placeslikee.domain.usecase.profile

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.AuthRepository
import com.example.placeslikee.domain.repositories.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetUsersMarkerUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val mapRepository: MapRepository
) {
    suspend operator fun invoke(): Flow<List<UIMarker>> {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            return mapRepository.getMarkersByUserId(userId)
        } else {
            return flowOf(emptyList())
        }
    }
}