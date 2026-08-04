package com.example.placeslikee.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("likes_table")
data class LikesEntity (
    @PrimaryKey
    val id: String,
    val markerId: String,
    val userId: String,
    val syncState: SyncState = SyncState.PENDING_CREATE,
    val localTimeStamp: Long = System.currentTimeMillis()
)