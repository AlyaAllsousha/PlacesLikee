package com.example.placeslikee.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.placeslikee.data.local.entities.pending.PendingAction
import com.example.placeslikee.data.local.entities.pending.PendingEntity

@Dao
interface PendingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPending(p: PendingEntity)
    @Query("SELECT * FROM pending_entity ORDER BY createdAt ASC")
    suspend fun getAllPending(): List<PendingEntity>
    @Query("DELETE FROM pending_entity WHERE id = :id")
    suspend fun deletePendingById(id: Int)

}