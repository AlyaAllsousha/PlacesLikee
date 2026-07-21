package com.example.placeslikee.data

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.entities.marks.SyncState
import com.example.placeslikee.data.mapper.toMarkerEntity
import com.example.placeslikee.data.mapper.toRemoteMarker
import com.example.placeslikee.data.mapper.toRemoteUser
import com.example.placeslikee.data.mapper.toUserEntity
import com.example.placeslikee.data.remote.RemoteDB
import com.google.firebase.firestore.core.ViewSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class MarkersSyncManager @Inject constructor(
    private val localDB: LocalDB,
    private val remoteDB: RemoteDB,
    private val connectivityManager: ConnectivityManager
) {
    suspend fun sync() {
        if (!isNetworkAvailable()) return
        val markerDao = localDB.markersDao()
        val userDao = localDB.usersDao()
        try {
            val remoteMarkers = remoteDB.getAllMarkers().associateBy { it.id }
            val unsyncedMarkers = markerDao.getMarksForSync()

            val remoteUsers = remoteDB.getAllUsers()
            for (user in remoteUsers) {
                val existingUser = userDao.getUserById(user.id)
                if (existingUser == null || existingUser.localTimestamp < user.remoteTimestamp)
                    userDao.createUser(user.toUserEntity())
            }

            for (dto in remoteMarkers) {
                val existingMarker = markerDao.getByIdSynced(dto.value.id)?.mark
                if (existingMarker == null || (dto.value.remoteTimestamp
                        ?: 0) > existingMarker.localTimestamp
                ) {
                    markerDao.createMark(dto.value.toMarkerEntity())
                }
            }

            for (marker in unsyncedMarkers) {
                val existingRemoteMark = remoteMarkers[marker.id]
                val remoteTimestamp = existingRemoteMark?.remoteTimestamp ?: 0L
                when (marker.synced) {
                    SyncState.PENDING_CREATE -> {
                        remoteDB.saveMarker(marker.toRemoteMarker())
                        if (marker.authorId != null)
                            remoteDB.saveUser(userDao.getUserById(marker.authorId)!!.toRemoteUser())
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
                        if(remoteTimestamp < marker.localTimestamp){
                            remoteDB.saveMarker(marker.toRemoteMarker())
                        }
                    }
                    SyncState.SYNCED -> {}
                }
                markerDao.markAsSynced(marker.id)
            }





        } catch (e: Exception) {
            Log.d("my log", "sync: $e")
        }

    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}