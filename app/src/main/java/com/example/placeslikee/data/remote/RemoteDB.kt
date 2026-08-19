package com.example.placeslikee.data.remote

import android.util.Log
import androidx.compose.runtime.currentComposer
import com.example.placeslikee.data.local.entities.LikesEntity
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.remote.dto.RemoteLike
import com.example.placeslikee.data.remote.dto.RemoteMarker
import com.example.placeslikee.data.remote.dto.RemoteUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDB @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collectionMarkers = firestore.collection("marker")
    private val collectionUsers = firestore.collection("users")
    private val collectionLikes = firestore.collection("likes")

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
        collectionMarkers.document(mark.id).set(mark).await()
    }
    suspend fun updateMarker(mark: RemoteMarker){
        collectionMarkers.document(mark.id)
            .update(
                "coordinates", mark.coordinates,
                "description", mark.description,
                "image", mark.image,
                "locationName", mark.locationName,
                "remoteTimestamp", mark.remoteTimestamp
            )
            .await()
    }

    fun deleteMarker(mark: RemoteMarker) {
        collectionMarkers.document(mark.id).delete()
    }



    suspend fun syncLikeTransaction(markerId: String, like: RemoteLike, isLiking: Boolean) {
        try {
            firestore.runTransaction { transaction ->
                val likeRef = collectionLikes.document(like.id)
                val markerRef = collectionMarkers.document(markerId)

                val existingLike = transaction.get(likeRef)
                val likeExists = existingLike.exists()

                if (isLiking && !likeExists) {
                    transaction.set(likeRef, like)
                    transaction.update(markerRef, "likesAmount", FieldValue.increment(1))
                    transaction.update(markerRef, "remoteTimestamp", System.currentTimeMillis())
                } else if (!isLiking && likeExists) {
                    transaction.delete(likeRef)
                    transaction.update(markerRef, "likesAmount", FieldValue.increment(-1))
                    transaction.update(markerRef, "remoteTimestamp", System.currentTimeMillis())
                }
            }.await()
        } catch (e: Exception) {
            Log.e("my log", "syncLikeTransaction error: $e")
            throw e
        }
    }

    suspend fun getAllUsers(): List<RemoteUser> {
        return try {
            val snapshot = collectionUsers.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(RemoteUser::class.java)
            }
        } catch (e: Exception) {
            Log.e("my log", "getAllUsers: Firebase error: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getUserById(userId: String): RemoteUser? {
        return try {
            collectionUsers.document(userId)
                .get()
                .await()
                .toObject(RemoteUser::class.java)
        } catch (e: Exception) {
            Log.e("my log", "getUserById: ${e.message}", e)
            null
        }
    }

    suspend fun saveUser(user: RemoteUser) {
        collectionUsers.document(user.id).set(user).await()
    }

    suspend fun getLikesByUserId(userId: String): List<RemoteLike> {
        return try {
            val snapshot = collectionLikes
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(RemoteLike::class.java)
            }
        } catch (e: Exception) {
            Log.e("my log", "getLikesByUserId: error: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getLikeById(likeId: String): RemoteLike?{
        return try {
            collectionLikes.document(likeId)
                .get()
                .await()
                .toObject(RemoteLike::class.java)
        } catch (e: Exception) {
            Log.e("my log", "getUserById: ${e.message}", e)
            null
        }
    }

    suspend fun saveLike(like: RemoteLike) {
        collectionLikes.document(like.id).set(like).await()
    }

    fun deleteLike(like: RemoteLike) {
        collectionLikes.document(like.id).delete()
    }

}