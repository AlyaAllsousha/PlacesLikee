package com.example.placeslikee.data.remote

import android.util.Log
import androidx.compose.runtime.currentComposer
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.remote.dto.RemoteMarker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDB @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collectionMarkers = firestore.collection("marker")
    private val collectionUsers = firestore.collection("users")


    suspend fun getAllMarkers(): List<RemoteMarker> {
        return try {
            val snapshot = collectionMarkers.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(RemoteMarker::class.java)
            }
        } catch (e: Exception) {
            Log.d("my log", "getAllMarkers: remote source error: $e")
            emptyList()
        }
    }

    suspend fun saveMarker(mark: RemoteMarker) {
        val markerWithTimestamp = mark.copy(remoteTimestamp = System.currentTimeMillis())
        collectionMarkers.document(mark.id).set(mark).await()
    }

    suspend fun deleteMarker(mark: RemoteMarker){
        collectionMarkers.document(mark.id).delete()
    }

    suspend fun getAllUsers(): List<UserEntity> {
        return try {
            val snapshot = collectionUsers.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(UserEntity::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveUser(user: UserEntity) {
        collectionUsers.document(user.id).set(user).await()
    }
}