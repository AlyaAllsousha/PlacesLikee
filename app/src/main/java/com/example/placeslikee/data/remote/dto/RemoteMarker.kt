package com.example.placeslikee.data.remote.dto

import com.google.firebase.firestore.GeoPoint


data class RemoteMarker(
    val id: String = "",
    val authorId: String = "",
    val coordinates: GeoPoint = GeoPoint(0.0, 0.0),
    val description: String? = null,
    val image: String? = null,
    val likesAmount: Int = 0,
    val locationName: String = ""
)
