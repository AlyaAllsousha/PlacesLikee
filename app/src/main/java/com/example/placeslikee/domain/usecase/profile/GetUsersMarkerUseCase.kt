package com.example.placeslikee.domain.usecase.profile

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.AuthRepository
import com.example.placeslikee.domain.repositories.MarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetUsersMarkerUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val markRepository: MarkRepository
) {
    operator fun invoke(): Flow<List<UIMarker>> {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            return markRepository.getMarkersByUserId(userId)
        } else {
            return flowOf(emptyList())
        }
    }
}