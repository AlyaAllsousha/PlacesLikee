package com.example.placeslikee.data.repository

import android.util.Log
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.entities.LikesEntity
import com.example.placeslikee.data.local.entities.SyncState
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.mapper.toUIMarker
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.LikeRepository
import com.example.placeslikee.workmanger.SyncWorkerScheduler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val localDB: LocalDB,
    private val syncWorkerScheduler: SyncWorkerScheduler

) : LikeRepository {
    private val mutex = Mutex()

    override suspend fun toggleLikeToMarker(markerId: String) {
        mutex.withLock {

            val userId = auth.currentUser?.uid ?: return
            val likeId = "${markerId}_${userId}"
            val marker = localDB.markersDao().getByIdSynced(markerId) ?: return

            val isCurrentlyLiked = marker.mark.likedByUser
            val newLikeCount = if (isCurrentlyLiked) {
                localDB.likesDao().markLikeAsUnliked(likeId)
                maxOf(0, marker.mark.likesAmount - 1)
            } else {
                localDB.likesDao().createLike(
                    LikesEntity(
                        id = likeId,
                        markerId = markerId,
                        userId = userId,
                        syncState = SyncState.PENDING_LIKED
                    )
                )
                marker.mark.likesAmount + 1
            }
            val updateMarker = marker.mark.copy(
                likesAmount = newLikeCount,
                likedByUser = !isCurrentlyLiked,
                localTimestamp = System.currentTimeMillis()
            )

            localDB.markersDao().updateMark(updateMarker)
            syncWorkerScheduler.scheduleSingleSync()
        }

    }

    override fun getUsersLikedMarkers(): Flow<List<UIMarker>> {
        val userId = auth.uid ?: return flowOf(emptyList())
        syncWorkerScheduler.scheduleSingleSync()
        return localDB.markersDao().getLikedMarkersByUser(userId).map { markers ->
            markers.map {
                it.toUIMarker().copy(likedByUser = true)
            }
        }

    }

}