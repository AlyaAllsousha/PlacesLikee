package com.example.placeslikee.domain.usecase.auth

import com.example.placeslikee.domain.repositories.AuthRepository
import javax.inject.Inject

class GetCurrentIdUseCase @Inject constructor(
    private val repository: AuthRepository
){
    operator fun invoke(): String? = repository.getCurrentUserId()
}