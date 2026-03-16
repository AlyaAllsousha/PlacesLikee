package com.example.placeslikee.di

import android.content.Context
import android.net.LocalSocket
import androidx.room.Room
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.dao.MarkerDao
import com.example.placeslikee.data.local.dao.PendingDao
import com.example.placeslikee.data.local.dao.UsersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): LocalDB{
        return Room.databaseBuilder(
            context,
            LocalDB::class.java,
            "placelikee_app_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideMarkerDao(database: LocalDB):MarkerDao{
        return database.markersDao()
    }
    @Provides
    fun provideUserDao(database: LocalDB):UsersDao{
        return database.usersDao()
    }
    @Provides
    fun providePendingDao(database: LocalDB):PendingDao{
        return database.pendingDao()
    }
}