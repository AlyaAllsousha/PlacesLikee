package com.example.placeslikee.domain.usecase.profile

import com.example.placeslikee.domain.repositories.AuthRepository
import javax.inject.Inject

class ChangeUserEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String>{
        return repository.changeUserEmail(email, password)
    }
}