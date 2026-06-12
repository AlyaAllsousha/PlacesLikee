package com.example.placeslikee.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.local.entities.marks.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createMark(mark: MarkerEntity)

    @Delete
    suspend fun deleteMark(mark: MarkerEntity)

    @Update
    suspend fun updateMark(mark: MarkerEntity)

    @Query("UPDATE marks_table SET synced = 'PENDING_LIKED' WHERE id = :id")
    suspend fun markAsLiked(id: Int)

    @Query("UPDATE marks_table SET synced = 'PENDING_UPDATE' WHERE id = :id")
    suspend fun markAsUpdated(id: Int)

    @Query("UPDATE marks_table SET synced = 'PENDING_CREATE' WHERE id = :id")
    suspend fun markAsCreated(id: Int)

    @Query("UPDATE marks_table SET synced = 'SYNCED' WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Query("UPDATE marks_table SET synced = 'PENDING_DELETE' WHERE id = :id")
    suspend fun markAsDeleted(id: Int)

    @Transaction
    @Query("SELECT * FROM marks_table WHERE synced != 'PENDING_DELETE'")
    fun getMarkers(): Flow<List<MarkerWithAuthor>>

    @Transaction
    @Query("SELECT * FROM marks_table WHERE id = :id")
    fun getById(id : Int): Flow<MarkerWithAuthor>

    @Query("SELECT * FROM marks_table WHERE synced != 'SYNCED'")
    fun getMarksForSync(): Flow<List<MarkerEntity>>

    @Query("UPDATE marks_table SET synced = :state WHERE id = :id")
    suspend fun updateSyncedState(id : Int, state : SyncState)

}