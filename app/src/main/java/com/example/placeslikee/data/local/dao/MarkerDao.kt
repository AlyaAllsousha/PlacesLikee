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
    suspend fun markAsLiked(id: String)

    @Query("UPDATE marks_table SET synced = 'PENDING_UNLIKED' WHERE id = :id")
    suspend fun markAsUnliked(id: String)


    @Query("UPDATE marks_table SET synced = 'PENDING_UPDATE' WHERE id = :id")
    suspend fun markAsUpdated(id: String)

    @Query("UPDATE marks_table SET synced = 'PENDING_CREATE' WHERE id = :id")
    suspend fun markAsCreated(id: String)

    @Query("UPDATE marks_table SET synced = 'SYNCED' WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE marks_table SET synced = 'PENDING_DELETE' WHERE id = :id")
    suspend fun markAsDeleted(id: String)

    @Transaction
    @Query("SELECT * FROM marks_table WHERE synced != 'PENDING_DELETE'")
    fun getMarkers(): Flow<List<MarkerWithAuthor>>

    @Transaction
    @Query("SELECT * FROM marks_table WHERE id = :id AND synced != 'PENDING_DELETE'")
    fun getById(id: String): Flow<MarkerWithAuthor?>

    @Transaction
    @Query("SELECT * FROM marks_table WHERE id = :id")
    suspend fun getByIdSynced(id: String): MarkerWithAuthor?


    @Transaction
    @Query("""
        SELECT marks_table.* FROM marks_table
        INNER JOIN likes_table ON marks_table.id = likes_table.markerId
        WHERE likes_table.userId = :userId
        AND likes_table.syncState != 'PENDING_UNLIKED'
        AND marks_table.synced != 'PENDING_DELETE'
    """)
    fun getLikedMarkersByUser(userId: String): Flow<List<MarkerWithAuthor>>

    @Transaction
    @Query("SELECT * FROM marks_table WHERE authorId = :userId AND synced != 'PENDING_DELETE'")
    fun getByUserId(userId: String): Flow<List<MarkerWithAuthor>>


    @Query("SELECT * FROM marks_table")
    fun getAllMarksForSync(): List<MarkerEntity>
}