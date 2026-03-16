package com.example.placeslikee.domain.models

data class MapPoint(
    val id: String,
    val latitude: Double,
    val longtitude: Double,
    val title: String,
    val isUserCreated: Boolean = false
)
