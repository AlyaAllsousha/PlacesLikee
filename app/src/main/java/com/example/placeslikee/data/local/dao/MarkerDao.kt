package com.example.placeslikee.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.placeslikee.data.local.entities.marks.MarkEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.local.entities.marks.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createMark(mark: MarkEntity)

    @Delete
    suspend fun deleteMark(mark: MarkEntity)

    @Query("UPDATE marks_table SET synced = 'PENDING_DELETE' WHERE id = :id")
    suspend fun markAsDeleted(id: Int)

    @Transaction
    @Query("SELECT * FROM marks_table WHERE synced != 'PENDING_DELETE'")
    fun getMarks(): Flow<List<MarkerWithAuthor>>

    @Query("SELECT * FROM marks_table WHERE id = :id")
    fun getById(id : Int): Flow<MarkEntity>

    @Query("UPDATE marks_table SET synced = :state WHERE id = :id")
    suspend fun updateSyncedState(id : Int, state : SyncState)

}