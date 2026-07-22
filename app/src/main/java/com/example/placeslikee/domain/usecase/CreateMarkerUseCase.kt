package com.example.placeslikee.domain.usecase

import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import java.util.UUID
import javax.inject.Inject

class CreateMarkerUseCase @Inject constructor(
    private val repository: MapRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lon: Double,
        name: String,
        authorId: String,
        description: String?,
        image: String?,
    ) {
        val newMarker = MarkerEntity(
            id = UUID.randomUUID().toString(),
            lat = lat,
            longitude = lon,
            name = name,
            authorId = authorId,
            description = description,
            image = image,
            localTimestamp = System.currentTimeMillis()
        )
        repository.addMarkers(newMarker)
    }
}