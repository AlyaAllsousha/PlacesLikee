package com.example.placeslikee.data.repository

import com.example.placeslikee.domain.models.MapPoint
import com.example.placeslikee.domain.repositories.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject


class MapRepositoryImpl @Inject constructor() :MapRepository {
    override fun getPoints(): Flow<List<MapPoint>> {
        return emptyFlow()
    }

    override suspend fun addPoint(lat: Double, lon: Double, title: String) {

    }
}