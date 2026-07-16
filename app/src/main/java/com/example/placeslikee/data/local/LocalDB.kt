package com.example.placeslikee.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.placeslikee.data.local.dao.MarkerDao
import com.example.placeslikee.data.local.dao.UsersDao
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.data.local.entities.marks.MarkerEntity

@Database(
    entities = [MarkerEntity::class, UserEntity::class],
    version = 3
)
@TypeConverters(MyTypeConverters::class)
abstract class LocalDB : RoomDatabase() {
    abstract fun markersDao():MarkerDao
    abstract fun usersDao(): UsersDao
}