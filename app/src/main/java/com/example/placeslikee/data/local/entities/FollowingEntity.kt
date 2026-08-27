package com.example.placeslikee.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "following_table")
data class FollowingEntity (
    @PrimaryKey val authorId: String,
    val authorName: String,
    val sync: SyncState = SyncState.PENDING_CREATE,
    val subscribedAt: Long
)