package com.example.placeslikee.domain.usecase.auth

import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class getCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val localDB: LocalDB
){
    operator fun invoke(): Flow<UserEntity?>{
        return repository.getCurrentUserIdFlow().flatMapLatest {userId ->
            if(userId == null)  {
                flowOf(null)
            }
            else{
                localDB.usersDao().getUserByIdFlow(userId)
            }

        }
    }
}