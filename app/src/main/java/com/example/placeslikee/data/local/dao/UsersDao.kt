package com.example.placeslikee.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.placeslikee.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(usersEntity: UserEntity)
    @Query("SELECT * FROM users_table WHERE id = :id")
    fun getUserById(id : String):Flow<UserEntity>
    @Delete
    suspend fun deleteUser(usersEntity: UserEntity)
}