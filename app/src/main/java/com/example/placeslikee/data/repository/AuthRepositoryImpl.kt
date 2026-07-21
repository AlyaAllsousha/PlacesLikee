package com.example.placeslikee.data.repository

import android.util.Log
import com.example.placeslikee.data.remote.RemoteDB
import com.example.placeslikee.data.remote.dto.RemoteUser
import com.example.placeslikee.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val remoteDB: RemoteDB
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return try{
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User not found")
            Result.success(userId)
        }
        catch (e: Exception){
            Log.d("my log", "AuthRepoImpl: Login failed: ${e.message} ")
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<String> {
        return try{
            val result  = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("Failed to create user")
            val newUser = RemoteUser(
                id = userId,
                name = name,
                remoteTimestamp = System.currentTimeMillis()
            )
            remoteDB.saveUser(newUser)
            Result.success(userId)
        }
        catch (e: Exception){
            Log.d("my log", "AuthRepoImpl: Registration failed: ${e.message} ")
            Result.failure(e)
        }

    }

    override fun getCurrentUserIdFlow(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener{firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose{auth.removeAuthStateListener(listener)}
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun logout() {
        auth.signOut()
    }
}