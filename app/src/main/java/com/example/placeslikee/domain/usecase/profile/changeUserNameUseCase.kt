package com.example.placeslikee.domain.usecase.profile

import com.example.placeslikee.domain.repositories.AuthRepository
import javax.inject.Inject

class changeUserNameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(name: String){
        val userId = authRepository.getCurrentUserId()
        authRepository.changeUserInfo(userId!!, name)
    }
}