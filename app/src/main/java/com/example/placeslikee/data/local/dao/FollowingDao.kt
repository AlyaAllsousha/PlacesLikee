package com.example.placeslikee.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.placeslikee.data.local.entities.FollowingEntity
import com.example.placeslikee.data.local.entities.LikesEntity
import com.example.placeslikee.data.local.entities.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(following: FollowingEntity)

    @Query("DELETE FROM following_table WHERE authorId = :authorId")
    suspend fun deleteSubscription(authorId: String)

    @Query("SELECT * FROM following_table")
    fun getSubscriptions(): Flow<List<FollowingEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM following_table WHERE authorId = :authorId AND sync != 'PENDING_DELETE')")
    fun observeIsSubscribed(authorId: String): Flow<Boolean>

    @Query("UPDATE following_table SET sync = 'SYNCED' WHERE authorId = :id AND sync = :oldState")
    suspend fun markFollowAsSynced(id: String, oldState: SyncState)

    @Query("UPDATE following_table SET sync = 'PENDING_DELETE' WHERE authorId = :id")
    suspend fun markFollowAsDeleted(id: String)

    @Query("DELETE FROM following_table")
    suspend fun deleteAllFollows()

    @Query("SELECT * FROM following_table")
    fun getAllFollowsForSync(): List<FollowingEntity>

    @Query("SELECT * FROM following_table WHERE authorId = :id")
    fun getFollowById(id: String): FollowingEntity?


}
