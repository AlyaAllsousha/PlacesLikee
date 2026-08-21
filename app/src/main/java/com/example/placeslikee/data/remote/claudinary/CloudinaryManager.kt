package com.example.placeslikee.data.remote.claudinary

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.get
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class CloudinaryManager @Inject constructor() {
    private val uploadPreset = "zloawzwu"

    suspend fun uploadImage(imageUri: Uri) :String = suspendCancellableCoroutine { continuation ->
        val requestId = MediaManager.get().upload(imageUri)
            .unsigned(uploadPreset)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {
                }

                override fun onSuccess(
                    requestId: String?,
                    resultData: Map<*, *>
                ) {
                    val secureUrl = resultData["secure_url"] as? String
                    if(secureUrl != null) {
                        if(continuation.isActive) {
                            continuation.resume(secureUrl)
                        }
                    }
                    else{
                        if(continuation.isActive){
                            continuation.resumeWithException(IllegalStateException("Claudinary: sucre_url is not found"))
                        }
                    }
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                    Log.e("my log", "CloudinaryManager: Upload error ${error?.description}")
                    if(continuation.isActive) {
                        continuation.resumeWithException(Exception(error?.description ?: "Upload error"))
                    }
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                    if(continuation.isActive){
                        continuation.resumeWithException(
                            Exception(error?.description ?: "Upload is rescheduled: no connection"))
                    }
                }
            })
            .dispatch()
        continuation.invokeOnCancellation {
            MediaManager.get().cancelRequest(requestId)
        }

    }
}