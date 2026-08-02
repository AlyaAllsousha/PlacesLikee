package com.example.placeslikee.domain.usecase.auth

import com.example.placeslikee.domain.repositories.AuthRepository
import javax.inject.Inject

class LogInUseCase @Inject  constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke(email: String, password: String): Result<String>{
        return repository.login(email, password)
    }
}