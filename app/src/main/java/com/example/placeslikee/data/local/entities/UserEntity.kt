package com.example.placeslikee.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity("users_table")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String = "",
    val localTimestamp: Long = System.currentTimeMillis()
)
