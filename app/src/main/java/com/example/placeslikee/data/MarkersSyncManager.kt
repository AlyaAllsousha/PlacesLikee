package com.example.placeslikee.data

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.entities.marks.SyncState
import com.example.placeslikee.data.mapper.toMarkerEntity
import com.example.placeslikee.data.mapper.toRemoteMarker
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
            val unsyncedMarkers = markerDao.getMarksForSync()
            for (marker in unsyncedMarkers) {
                when (marker.synced) {
                    SyncState.PENDING_CREATE -> {
                        remoteDB.saveMarker(marker.toRemoteMarker())
                        remoteDB.saveUser(userDao.getUserById(marker.authorId))
                    }

                    SyncState.PENDING_LIKED -> {
                        remoteDB.saveMarker(
                            marker.toRemoteMarker().copy(likesAmount = marker.likesAmount++)
                        )
                    }

                    SyncState.PENDING_DELETE -> {
                        remoteDB.deleteMarker(marker.toRemoteMarker())
                        localDB.markersDao().deleteMark(marker)
                    }

                    SyncState.PENDING_UPDATE -> {}
                    SyncState.SYNCED -> TODO()

                }
                markerDao.markAsSynced(marker.id)
            }
            val remoteMarkers = remoteDB.getAllMarkers()
            for (dto in remoteMarkers) {
                val existingMarker = markerDao.getByIdSynced(dto.id)
                if(existingMarker == null || (dto.remoteTimestamp ?: 0) > existingMarker.mark.localTimestamp)
                    markerDao.createMark(dto.toMarkerEntity())
            }
            val remoteUsers = remoteDB.getAllUsers()
            for (user in remoteUsers) {
                userDao.createUser(user)
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