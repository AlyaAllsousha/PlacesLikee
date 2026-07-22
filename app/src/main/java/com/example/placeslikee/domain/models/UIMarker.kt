package com.example.placeslikee.domain.models

import com.google.firebase.Timestamp

data class UIMarker(
    val id: String,
    val lat: Double,
    val longitude: Double,
    val name:String,
    val authorName: String?,
    val authorId: String = "",
    val description: String?,
    val likesAmount: Int = 0,
    val likedByUser: Boolean = false,
    val image: String?,
    val uiTimestamp: Long
)