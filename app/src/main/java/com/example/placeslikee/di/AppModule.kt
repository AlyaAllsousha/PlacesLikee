package com.example.placeslikee.di

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
    fun provideMapRepository():MapRepository{
        return MapRepositoryImpl()
    }


}