package com.example.placeslikee.data.local.entities.pending

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("pending_entity")
data class PendingEntity (
    @PrimaryKey(autoGenerate = true)
    val id:Int? = null,
    val markId: String,
    val authorId: String,
    val action: PendingAction,
    val createdAt: Long = System.currentTimeMillis()
)
