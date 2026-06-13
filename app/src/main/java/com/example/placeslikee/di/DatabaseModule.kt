package com.example.placeslikee.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.LocalSocket
import androidx.room.Room
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.local.dao.MarkerDao
import com.example.placeslikee.data.local.dao.UsersDao
import com.google.firebase.firestore.FirebaseFirestore
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
    @Singleton
    fun provideFirestore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager{
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    @Provides
    fun provideMarkerDao(database: LocalDB):MarkerDao{
        return database.markersDao()
    }
    @Provides
    fun provideUserDao(database: LocalDB):UsersDao{
        return database.usersDao()
    }


}