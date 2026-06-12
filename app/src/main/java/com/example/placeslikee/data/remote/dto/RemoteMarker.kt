package com.example.placeslikee.data.remote.dto

import com.google.firebase.firestore.GeoPoint


data class RemoteMarker(
    val id: String,
    val authorId: String,
    val coordinates: GeoPoint,
    val description: String?,
    val image: String?,
    val likesAmount: Int,
    val locationName: String
)
