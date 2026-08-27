package com.example.placeslikee.data.remote.notifications

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VercelApi {
    @POST("api/notify")
    suspend fun sendNewMarkerNotification(
        @Body request: PushNotificationRequest
    ): Response<Unit>
}