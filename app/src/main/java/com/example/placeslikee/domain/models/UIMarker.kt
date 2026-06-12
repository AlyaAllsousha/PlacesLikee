package com.example.placeslikee.domain.models

data class UIMarker(
    val id: String,
    val lat: Double,
    val longitude: Double,
    val name:String,
    val authorName: String?,
    val description: String?,
    val likesAmount: Int,
    val likedByUser: Boolean = false,
    val image: String?
)