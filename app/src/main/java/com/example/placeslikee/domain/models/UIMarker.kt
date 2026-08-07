package com.example.placeslikee.domain.models

data class UIMarker(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val name:String,
    val authorName: String?  ,
    val authorId: String = "",
    val description: String?,
    val likesAmount: Int = 0,
    val likedByUser: Boolean = false,
    val image: String?,
    val uiTimestamp: Long
)