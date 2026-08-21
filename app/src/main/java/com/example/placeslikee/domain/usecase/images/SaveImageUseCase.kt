package com.example.placeslikee.domain.usecase.images

import com.example.placeslikee.domain.repositories.ImageStorageRepository
import javax.inject.Inject

class SaveImageUseCase @Inject constructor(
    private val repository: ImageStorageRepository
)  {
    suspend operator fun invoke (uriString : String) : String?{
        return repository.saveImageLocally(uriString)
    }
}