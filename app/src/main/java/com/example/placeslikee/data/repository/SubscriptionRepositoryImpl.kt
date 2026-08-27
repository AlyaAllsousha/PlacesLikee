package com.example.placeslikee.data.repository

import android.util.Log
import com.example.placeslikee.data.local.dao.FollowingDao
import com.example.placeslikee.data.local.entities.FollowingEntity
import com.example.placeslikee.data.local.entities.SyncState
import com.example.placeslikee.data.remote.RemoteDB
import com.example.placeslikee.data.remote.dto.RemoteFollowing
import com.example.placeslikee.domain.repositories.SubscriptionsRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SubscriptionsRepositoryImpl @Inject constructor(
    private val remoteDB: RemoteDB,
    private val followingDao: FollowingDao,
    private val auth: FirebaseAuth
) : SubscriptionsRepository {

    override suspend fun subscribeToAuthor(authorId: String, authorName: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val follow = FollowingEntity(
            authorId = authorId,
            authorName = authorName,
            subscribedAt = System.currentTimeMillis()
        )
        followingDao.insertSubscription(follow)
        try {
            Firebase.messaging.subscribeToTopic("author_$authorId").await()
            val remoteFollow = RemoteFollowing(authorId, authorName)
            remoteDB.saveFollow(currentUserId, remoteFollow)
            followingDao.markFollowAsSynced(authorId, SyncState.PENDING_CREATE)
        } catch (e: Exception) {
            Log.e("my log", "subscribeToAuthor: $e", )
            throw e
        }
    }

    override suspend fun unsubscribeFromAuthor(authorId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        followingDao.markFollowAsDeleted(authorId)
        try {
            remoteDB.deleteFollow(currentUserId, authorId)
            followingDao.deleteSubscription(authorId)
            Firebase.messaging.unsubscribeFromTopic("author_$authorId").await()

        } catch (e: Exception) {
            Log.e("my log", "unsubscribeFromAuthor: $e", )
            throw e
        }
    }

    override fun getFollowingList(): Flow<List<FollowingEntity>> {
        return followingDao.getSubscriptions()
    }

    override fun observeIsSubscribed(authorId: String): Flow<Boolean> {
        return followingDao.observeIsSubscribed(authorId)
    }
}