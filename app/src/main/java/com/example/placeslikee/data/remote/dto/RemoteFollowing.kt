package com.example.placeslikee.data.remote.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class RemoteFollowing (
    val authorId: String = "",
    val authorName: String = "",
    @ServerTimestamp
    val subscribedAt: Date? = null
)