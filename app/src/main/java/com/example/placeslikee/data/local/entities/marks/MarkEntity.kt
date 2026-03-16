package com.example.placeslikee.data.local.entities.marks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("marks_table")
data class MarkEntity(
   @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val remoteId: String?= null,
    val lat: Double,
    val longitude: Double,
    val name:String,
    val authorId: String? = null,
    val description: String?,
    val likesAmount: Int,
    val likedByUser: Boolean = false,
    val image: String?,
    val synced: SyncState = SyncState.SYNCED
)
