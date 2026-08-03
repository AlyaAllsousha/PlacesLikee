package com.example.placeslikee.domain.usecase.profile

import com.example.placeslikee.domain.repositories.AuthRepository
import javax.inject.Inject

class SyncAuthDataUseCase @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator  fun invoke(){
        repository.syncAuthData()
    }
}