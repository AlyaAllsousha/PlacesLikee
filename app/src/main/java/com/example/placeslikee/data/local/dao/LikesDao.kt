package com.example.placeslikee.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.placeslikee.data.local.entities.LikesEntity
import com.example.placeslikee.data.local.entities.SyncState
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import kotlinx.coroutines.flow.Flow

@Dao
interface LikesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLike(newLike: LikesEntity)

    @Delete
    suspend fun deleteLike(like: LikesEntity)

    @Query("UPDATE likes_table SET syncState = 'SYNCED' WHERE id = :id AND syncState = :oldState")
    suspend fun markLikeAsSynced(id: String, oldState: SyncState)

    @Query("UPDATE likes_table SET syncState = 'PENDING_UNLIKED' WHERE id = :id")
    suspend fun markLikeAsUnliked(id: String)


    @Query("DELETE FROM likes_table")
    suspend fun deleteAllLikes()

    @Query("SELECT * FROM likes_table WHERE id = :id")
    fun getLikeById(id: String): LikesEntity?

    @Query("SELECT * FROM likes_table")
    fun getAllLikesForSync(): List<LikesEntity>


}