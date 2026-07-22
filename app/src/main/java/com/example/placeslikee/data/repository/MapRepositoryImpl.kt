package com.example.placeslikee.data.repository

import android.util.Log
import com.example.placeslikee.data.MarkersSyncManager
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.mapper.toMarkerEntity
import com.example.placeslikee.data.mapper.toUIMarker
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


class MapRepositoryImpl @Inject constructor(
    private val localDb: LocalDB,
    private val syncManager: MarkersSyncManager
) :MapRepository {
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getMarkers(): Flow<List<UIMarker>> {
        repositoryScope.launch {
            syncManager.sync()
        }
        return localDb.markersDao().getMarkers().map { markers ->
            markers.map {
                it.toUIMarker()
            }
        }
    }

    override suspend fun addMarkers(newMarker: MarkerEntity) {
        val marker = newMarker
        localDb.markersDao().createMark(marker)
        syncManager.sync()
    }


    override suspend fun deleteMark(marker: UIMarker) {
        localDb.markersDao().markAsDeleted(marker.id)
        syncManager.sync()
    }
}