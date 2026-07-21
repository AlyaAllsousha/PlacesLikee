package com.example.placeslikee.data.remote.dto

import androidx.room.PrimaryKey

data class RemoteUser(
    val id: String = "",
    val name: String = "",
    val remoteTimestamp: Long = 0L
)
