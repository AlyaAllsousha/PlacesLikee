package com.example.placeslikee.domain.repositories

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(email: String, password: String, name: String): Result<String>
    fun getCurrentUserId(): String?
    fun getCurrentUserIdFlow(): Flow<String?>
    fun getCurrentUserEmail(): String?
    fun isUserLoggedIn(): Boolean
    fun logout()
}