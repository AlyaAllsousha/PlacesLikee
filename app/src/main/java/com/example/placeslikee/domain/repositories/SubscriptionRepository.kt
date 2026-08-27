package com.example.placeslikee.domain.repositories

import com.example.placeslikee.data.local.entities.FollowingEntity
import kotlinx.coroutines.flow.Flow

interface SubscriptionsRepository {
    suspend fun subscribeToAuthor(authorId: String, authorName: String)
    suspend fun unsubscribeFromAuthor(authorId: String)
    fun getFollowingList(): Flow<List<FollowingEntity>>
    fun observeIsSubscribed(authorId: String): Flow<Boolean>

}