package com.example.placeslikee.domain.usecase.auth

import com.example.placeslikee.domain.repositories.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend  operator fun invoke() = repository.logout()
}