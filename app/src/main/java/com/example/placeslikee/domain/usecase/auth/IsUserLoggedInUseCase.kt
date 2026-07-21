package com.example.placeslikee.domain.usecase.auth


import com.example.placeslikee.domain.repositories.AuthRepository
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() : Boolean = repository.isUserLoggedIn()
}