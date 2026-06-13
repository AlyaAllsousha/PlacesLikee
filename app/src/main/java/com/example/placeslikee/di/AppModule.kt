package com.example.placeslikee.di

import com.example.placeslikee.data.MarkersSyncManager
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.repository.MapRepositoryImpl
import com.example.placeslikee.domain.repositories.MapRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMapRepository(localDb: LocalDB, syncManager: MarkersSyncManager):MapRepository{
        return MapRepositoryImpl(localDb, syncManager)
    }


}