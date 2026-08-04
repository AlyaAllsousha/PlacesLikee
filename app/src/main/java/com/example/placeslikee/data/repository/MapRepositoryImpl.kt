package com.example.placeslikee.data.repository

import android.util.Log
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.mapper.toUIMarker
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import com.example.placeslikee.workmanger.SyncWorkerScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class MapRepositoryImpl @Inject constructor(
    private val localDb: LocalDB,
    private val syncScheduler: SyncWorkerScheduler,
) : MapRepository {

    override fun getMarkers(): Flow<List<UIMarker>> {
        syncScheduler.scheduleSingleSync()
        return localDb.markersDao().getMarkers().map { markers ->
            markers.map {
                it.toUIMarker()
            }
        }
    }

    override suspend fun addMarkers(newMarker: MarkerEntity) {
        val marker = newMarker
        localDb.markersDao().createMark(marker)
        syncScheduler.scheduleSingleSync()
    }


    override suspend fun deleteMark(id: String) {
        localDb.markersDao().markAsDeleted(id)
        syncScheduler.scheduleSingleSync()
    }

    override fun getMarkerById(markerId: String): Flow<UIMarker?> {
        syncScheduler.scheduleSingleSync()
        return localDb.markersDao().getById(markerId).map{
            it?.toUIMarker()
        }
    }

    override suspend fun getMarkersByUserId(userId: String): Flow<List<UIMarker>> {
        syncScheduler.scheduleSingleSync()
        return localDb.markersDao().getByUserId(userId).map {
            it.map {
                it.toUIMarker()
            }
        }
    }



    override suspend fun refresh() {
        syncScheduler.scheduleSingleSync()
    }

}