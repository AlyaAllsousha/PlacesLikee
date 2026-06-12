package com.example.placeslikee.data.repository

import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject


class MapRepositoryImpl @Inject constructor() :MapRepository {
    override fun getMarkers(): Flow<List<UIMarker>> {
        return emptyFlow()
    }

    override suspend fun addMarkers(newMarker: UIMarker) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMarker(marker: UIMarker) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMark(marker: UIMarker) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshMarkers() {
        TODO("Not yet implemented")
    }


}