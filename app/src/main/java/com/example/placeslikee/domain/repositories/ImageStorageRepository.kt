package com.example.placeslikee.domain.repositories

interface ImageStorageRepository {
    suspend fun saveImageLocally(uriString: String) : String?
    suspend fun uploadImage(imageUriString: String) : String
}