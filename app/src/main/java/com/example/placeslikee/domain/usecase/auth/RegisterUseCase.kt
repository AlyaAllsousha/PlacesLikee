package com.example.placeslikee.domain.usecase.auth

import com.example.placeslikee.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<String>{
        return repository.register(email, password, name)
    }
}