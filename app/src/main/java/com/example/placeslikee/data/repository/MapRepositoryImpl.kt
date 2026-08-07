package com.example.placeslikee.data.repository

import android.util.Log
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.mapper.toMarkerEntity
import com.example.placeslikee.data.mapper.toUIMarker
import com.example.placeslikee.domain.models.UIMarker
import com.example.placeslikee.domain.repositories.MapRepository
import com.example.placeslikee.workmanger.SyncWorkerScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


class MapRepositoryImpl @Inject constructor(
    private val localDb: LocalDB,
    private val syncScheduler: SyncWorkerScheduler,
) : MapRepository {

    override fun getMarkers(): Flow<List<UIMarker>> {
        return localDb.markersDao().getMarkers()
            .map { markers -> markers.map { it.toUIMarker() } }
            .onStart { syncScheduler.scheduleSingleSync() }

    }

    override fun getMarkerById(markerId: String): Flow<UIMarker?> {
        return localDb.markersDao().getById(markerId)
            .map { it?.toUIMarker() }
            .onStart { syncScheduler.scheduleSingleSync() }
    }


    override fun getMarkersByUserId(userId: String): Flow<List<UIMarker>> {
        return localDb.markersDao().getByUserId(userId)
            .map {
                it.map { it.toUIMarker() }
            }
            .onStart { syncScheduler.scheduleSingleSync() }

    }


    override suspend fun addMarkers(newMarker: MarkerEntity):Result<Unit> {
        return runCatching{
            localDb.markersDao().createMark(newMarker)
            syncScheduler.scheduleSingleSync()
            Result.success(Unit)
        }
    }


    override suspend fun editMarker(marker: UIMarker): Result<String> {
        return try {
            localDb.markersDao().updateMark(marker.toMarkerEntity())
            syncScheduler.scheduleSingleSync()
            Result.success(marker.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun deleteMark(id: String): Result<Unit> {
        return runCatching {
            localDb.markersDao().markAsDeleted(id)
            syncScheduler.scheduleSingleSync()
            Result.success(Unit)
        }
    }


    override suspend fun refresh() {
        syncScheduler.scheduleSingleSync()
    }

}