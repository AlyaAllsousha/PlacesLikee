package com.example.placeslikee.workmanger

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import com.example.placeslikee.data.remote.claudinary.CloudinaryManager
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.dao.LikesDao
import com.example.placeslikee.data.local.dao.MarkerDao
import com.example.placeslikee.data.local.dao.UsersDao
import com.example.placeslikee.data.local.entities.SyncState
import com.example.placeslikee.data.mapper.toLikeEntity
import com.example.placeslikee.data.mapper.toMarkerEntity
import com.example.placeslikee.data.mapper.toRemoteLike
import com.example.placeslikee.data.mapper.toRemoteMarker
import com.example.placeslikee.data.mapper.toRemoteUser
import com.example.placeslikee.data.mapper.toUserEntity
import com.example.placeslikee.data.remote.RemoteDB
import com.example.placeslikee.data.remote.dto.RemoteMarker
import com.example.placeslikee.data.remote.dto.RemoteUser
import com.example.placeslikee.domain.repositories.AuthRepository
import com.example.placeslikee.domain.repositories.ImageStorageRepository
import com.example.placeslikee.domain.repositories.LikeRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.collections.iterator

class MarkersSyncManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val localDB: LocalDB,
    private val remoteDB: RemoteDB,
    private val connectivityManager: ConnectivityManager,
    private val imageStorageRepository: ImageStorageRepository
) {
    suspend fun sync() {
        if (!isNetworkAvailable()) {
            throw NoNetworkException()
        }

        val markerDao = localDB.markersDao()
        val userDao = localDB.usersDao()
        val likesDao = localDB.likesDao()
        try {
            val remoteMarkers = remoteDB.getAllMarkers().associateBy { it.id }
            val remoteUsers = remoteDB.getAllUsers().associateBy { it.id }

            pullRemoteChanges(markerDao, userDao, likesDao, remoteMarkers)
            pushLocalChanges(markerDao, userDao, remoteMarkers, remoteUsers)

        } catch (e: Exception) {
            Log.e("my log", "sync: $e", e)
            throw e
        }

    }

    private suspend fun pushLocalChanges(
        markersDao: MarkerDao,
        userDao: UsersDao,
        remoteMarkers: Map<String, RemoteMarker>,
        remoteUsers: Map<String, RemoteUser>
    ) {
        val unsyncedMarkers = markersDao.getAllMarksForSync()

        for (marker in unsyncedMarkers) {
            val existingRemoteMark = remoteMarkers[marker.id]
            val remoteTimestamp = existingRemoteMark?.remoteTimestamp ?: 0L
            when (marker.synced) {
                SyncState.PENDING_CREATE -> {
                    var remoteImageUrl  = marker.image
                    if(!marker.image.isNullOrEmpty()){
                        try{
                            remoteImageUrl = imageStorageRepository.uploadImage(marker.image)
                            localDB.markersDao().updateMark(marker.copy(image = remoteImageUrl))
                        }
                        catch (e: Exception){
                            Log.e("my log", "pushLocalChanges: Image upload failed $e")
                            throw e
                        }
                    }
                    val remoteMarker = marker.toRemoteMarker().copy(image = remoteImageUrl)
                    remoteDB.saveMarker(remoteMarker)
                    if (marker.authorId != null)
                        remoteDB.saveUser(
                            userDao.getUserById(marker.authorId)!!.toRemoteUser()
                        )
                }

                SyncState.PENDING_DELETE -> {
                    remoteDB.deleteMarker(marker.toRemoteMarker())
                    localDB.markersDao().deleteMark(marker)
                }

                SyncState.PENDING_UPDATE -> {
                    if (remoteTimestamp < marker.localTimestamp) {
                        var remoteImageUrl  = marker.image
                        if(!marker.image.isNullOrEmpty() && !marker.image.startsWith("http")){
                            try{
                                remoteImageUrl = imageStorageRepository.uploadImage(marker.image)
                                localDB.markersDao().updateMark(marker.copy(image = remoteImageUrl))
                            }
                            catch (e: Exception){
                                Log.e("my log", "pushLocalChanges: Image upload failed $e")
                                throw e
                            }
                        }
                        val remoteMarker = marker.toRemoteMarker().copy(image = remoteImageUrl)
                        remoteDB.updateMarker(remoteMarker)
                    }
                }

                SyncState.SYNCED -> {
                    if (existingRemoteMark == null) {
                        localDB.markersDao().deleteMark(marker)
                    }
                }

                SyncState.PENDING_LIKED, SyncState.PENDING_UNLIKED -> {}

            }
            markersDao.markAsSynced(marker.id)
        }

        val localUsers = userDao.getAllUsers()
        val currUserId = auth.uid
        if (currUserId != null) {
            val currUser = userDao.getUserById(currUserId)
            val remoteUser = remoteDB.getUserById(currUserId)
            if (currUser != null && ((remoteUser?.remoteTimestamp ?: 0L) < currUser.localTimestamp))
                remoteDB.saveUser(currUser.toRemoteUser())

        }
        for (user in localUsers) {
            val existingUser = remoteUsers[user.id]
            if (user.syncState == SyncState.SYNCED && existingUser == null) {
                userDao.deleteUser(user)
            }
        }

        val unsyncedLikes = localDB.likesDao().getAllLikesForSync()

        for (like in unsyncedLikes) {
            if (like.syncState == SyncState.PENDING_LIKED) {
                remoteDB.syncLikeTransaction(like.markerId, like.toRemoteLike(), true)
                localDB.likesDao().markLikeAsSynced(like.id, SyncState.PENDING_LIKED)
            } else if (like.syncState == SyncState.PENDING_UNLIKED) {
                remoteDB.syncLikeTransaction(like.markerId, like.toRemoteLike(), false)
                localDB.likesDao().deleteLike(like)

            } else if (like.syncState == SyncState.SYNCED) {
                val existingLike = remoteDB.getLikeById(like.id)
                if (existingLike == null) {
                    localDB.likesDao().deleteLike(like)
                }
            }

        }
    }

    private suspend fun pullRemoteChanges(
        markerDao: MarkerDao,
        userDao: UsersDao,
        likeDB: LikesDao,
        remoteMarker: Map<String, RemoteMarker>
    ) {
        val remoteUsers = remoteDB.getAllUsers()
        authRepository.syncAuthData()
        for (user in remoteUsers) {
            val existingUser = userDao.getUserById(user.id)
            if (existingUser == null || existingUser.localTimestamp < user.remoteTimestamp)
                userDao.createUser(user.toUserEntity())
        }

        for (dto in remoteMarker) {
            val existingMarker = markerDao.getByIdSynced(dto.value.id)?.mark
            if (existingMarker == null || (dto.value.remoteTimestamp
                    ?: 0) > existingMarker.localTimestamp
            ) {
                val newMarkerEntity = dto.value.toMarkerEntity().copy(
                    likedByUser = existingMarker?.likedByUser ?: false
                )
                markerDao.createMark(newMarkerEntity)
            }
        }
        if (auth.uid != null) {
            val remoteLikes = remoteDB.getLikesByUserId(auth.uid!!)
            for (like in remoteLikes) {
                val existingLike = likeDB.getLikeById(like.id)
                if (existingLike == null || (like.remoteTimestamp
                        ?: 0) > existingLike.localTimeStamp
                )
                    localDB.likesDao().createLike(like.toLikeEntity())
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

class NoNetworkException : Exception("No network available")