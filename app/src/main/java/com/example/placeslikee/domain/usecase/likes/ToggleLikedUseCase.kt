package com.example.placeslikee.domain.usecase.likes

import com.example.placeslikee.domain.repositories.LikeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ToggleLikedUseCase @Inject constructor(
    private val repository: LikeRepository
) {
    suspend operator fun invoke(markId: String){
        repository.toggleLikeToMarker(markId)
    }
}