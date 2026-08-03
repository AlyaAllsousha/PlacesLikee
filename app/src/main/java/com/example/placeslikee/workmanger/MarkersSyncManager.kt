package com.example.placeslikee.workmanger

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.dao.MarkerDao
import com.example.placeslikee.data.local.dao.UsersDao
import com.example.placeslikee.data.local.entities.marks.SyncState
import com.example.placeslikee.data.mapper.toMarkerEntity
import com.example.placeslikee.data.mapper.toRemoteMarker
import com.example.placeslikee.data.mapper.toRemoteUser
import com.example.placeslikee.data.mapper.toUserEntity
import com.example.placeslikee.data.remote.RemoteDB
import com.example.placeslikee.data.remote.dto.RemoteMarker
import com.example.placeslikee.domain.repositories.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.collections.iterator

class MarkersSyncManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val localDB: LocalDB,
    private val remoteDB: RemoteDB,
    private val connectivityManager: ConnectivityManager
) {
    suspend fun sync() {
        if (!isNetworkAvailable()) {
            throw NoNetworkException()
        }

        val markerDao = localDB.markersDao()
        val userDao = localDB.usersDao()

        try {
            val remoteMarkers = remoteDB.getAllMarkers().associateBy { it.id }
            pullRemoteChanges(markerDao, userDao, remoteMarkers)
            pushLocalChanges(markerDao, userDao, remoteMarkers)

        } catch (e: Exception) {
            Log.d("my log", "sync: $e")
            throw  e
        }

    }
    private suspend fun pushLocalChanges(markersDao: MarkerDao, userDao: UsersDao, remoteMarkers:  Map<String, RemoteMarker>){
        val unsyncedMarkers = markersDao.getAllMarksForSync()

        for (marker in unsyncedMarkers) {
            val existingRemoteMark = remoteMarkers[marker.id]
            val remoteTimestamp = existingRemoteMark?.remoteTimestamp ?: 0L
            when (marker.synced) {
                SyncState.PENDING_CREATE -> {
                    remoteDB.saveMarker(marker.toRemoteMarker())
                    if (marker.authorId != null)
                        remoteDB.saveUser(
                            userDao.getUserById(marker.authorId)!!.toRemoteUser()
                        )
                }

                SyncState.PENDING_LIKED -> {
                    remoteDB.saveMarker(
                        marker.toRemoteMarker().copy(likesAmount = marker.likesAmount)
                    )
                }

                SyncState.PENDING_DELETE -> {
                    remoteDB.deleteMarker(marker.toRemoteMarker())
                    localDB.markersDao().deleteMark(marker)
                }

                SyncState.PENDING_UPDATE -> {
                    if (remoteTimestamp < marker.localTimestamp) {
                        remoteDB.saveMarker(marker.toRemoteMarker())
                    }
                }

                SyncState.SYNCED -> {
                    if (existingRemoteMark == null) {
                        localDB.markersDao().deleteMark(marker)
                    }
                }
            }
            markersDao.markAsSynced(marker.id)
        }

        val currUserId = auth.uid
        if(currUserId != null){
            val currUser = userDao.getUserById(currUserId)
            val remoteUser = remoteDB.getUserById(currUserId)
            if(currUser != null && ((remoteUser?.remoteTimestamp ?: 0L) < currUser.localTimestamp))
                remoteDB.saveUser(currUser.toRemoteUser())
        }

    }
    private suspend fun pullRemoteChanges(markerDao: MarkerDao, userDao: UsersDao, remoteMarker:  Map<String, RemoteMarker>){
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
                markerDao.createMark(dto.value.toMarkerEntity())
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