package com.example.placeslikee.data.repository

import android.util.Log
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.graphics.RectangleShape
import com.example.placeslikee.workmanger.MarkersSyncManager
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.mapper.toUserEntity
import com.example.placeslikee.data.remote.RemoteDB
import com.example.placeslikee.data.remote.dto.RemoteUser
import com.example.placeslikee.domain.repositories.AuthRepository
import com.example.placeslikee.workmanger.SyncWorkerScheduler
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val remoteDB: RemoteDB,
    private val localDB: LocalDB,
    private val syncScheduler: SyncWorkerScheduler
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User not found")
            syncUserData(userId)
            syncScheduler.scheduleSingleSync()

            Result.success(userId)
        } catch (e: Exception) {
            Log.d("my log", "AuthRepoImpl: Login failed: ${e.message} ")
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("Failed to create user")
            val newUser = RemoteUser(
                id = userId,
                name = name,
                email = email,
                remoteTimestamp = System.currentTimeMillis()
            )
            remoteDB.saveUser(newUser)
            localDB.usersDao().createUser(newUser.toUserEntity())
            syncScheduler.scheduleSingleSync()
            Result.success(userId)
        } catch (e: Exception) {
            Log.d("my log", "AuthRepoImpl: Registration failed: ${e.message} ")
            Result.failure(e)
        }

    }

    override fun getCurrentUserIdFlow(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
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

    override suspend fun changeUserInfo(id: String, name: String) {
        val currUser = localDB.usersDao().getUserById(id)
        localDB.usersDao()
            .createUser(currUser!!.copy(name = name, localTimestamp = System.currentTimeMillis()))
        syncScheduler.scheduleSingleSync()
    }

    override suspend fun changeUserEmail(email: String, password: String): Result<String> {
        val user = auth.currentUser
        if (user == null || user.email == null) {
            return Result.failure(NotAuthorizedException())
        }
        val localUser = localDB.usersDao().getUserById(user.uid)
            ?: return Result.failure(NoUserFoundException())
        return try {
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).await()

            user.verifyBeforeUpdateEmail(email).await()
            syncScheduler.scheduleSingleSync()
            Result.success(email)
        } catch (e: Exception) {
            Log.e("my log", "changeUserEmail: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                localDB.likesDao().deleteAllLikes()
                localDB.markersDao().deleteLikeForLogout()
                localDB.followingDao().deleteAllFollows()

                Firebase.messaging.deleteToken().await()

            } catch (e: Exception) {
                Log.e("Logout", "Ошибка при очистке данных: ${e.message}")
            } finally {
                auth.signOut()
            }
        }
    }

    override suspend fun syncAuthData() {
        val user = auth.currentUser ?: return
        try {
            user.reload().await()
            val actualFirebaseEmail = auth.currentUser?.email ?: return
            val localUser = localDB.usersDao().getUserById(user.uid) ?: return

            if (localUser.email != actualFirebaseEmail) {
                localDB.usersDao().createUser(
                    localUser.copy(
                        email = actualFirebaseEmail,
                        localTimestamp = System.currentTimeMillis()
                    )
                )
            }
        } catch (e: Exception) {
        }
    }

    private suspend fun syncUserData(userId: String) {
        try {
            val remoteUser = remoteDB.getUserById(userId)

            if (remoteUser != null) {
                localDB.usersDao().createUser(remoteUser.toUserEntity())
            }
        } catch (e: Exception) {
            Log.e("my log", "syncUserData: failed - ${e.message}", e)
        }

    }

}

class NotAuthorizedException : Exception("Пользователь не авторизован")
class NoUserFoundException : Exception("Пользователь не найден на устройстве")
