package com.example.placeslikee.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.placeslikee.data.local.dao.MarkerDao
import com.example.placeslikee.data.local.dao.PendingDao
import com.example.placeslikee.data.local.dao.UsersDao
import com.example.placeslikee.data.local.entities.pending.PendingEntity
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.data.local.entities.marks.MarkEntity

@Database(
    entities = [MarkEntity::class, PendingEntity::class, UserEntity::class],
    version = 1
)
@TypeConverters(MyTypeConverters::class)
abstract class LocalDB : RoomDatabase() {
    abstract fun markersDao():MarkerDao
    abstract fun usersDao(): UsersDao
    abstract fun pendingDao(): PendingDao

}