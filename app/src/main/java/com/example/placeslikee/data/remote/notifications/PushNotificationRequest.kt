package com.example.placeslikee.data.remote.notifications

data class PushNotificationRequest(
    val authorId: String,
    val authorName: String,
    val markerName: String,
    val markerId: String,
)
