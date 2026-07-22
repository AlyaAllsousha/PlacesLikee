package com.example.placeslikee.data.local.entities.marks

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity("marks_table")
data class MarkerEntity(
   @PrimaryKey
    val id: String,
    val lat: Double,
    val longitude: Double,
    val name:String,
    val authorId: String?,
    val description: String?,
    var likesAmount: Int = 0,
    val likedByUser: Boolean = false,
    val image: String?,
    val synced: SyncState = SyncState.PENDING_CREATE,
    val localTimestamp: Long = System.currentTimeMillis()
)
