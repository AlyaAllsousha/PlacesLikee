package com.example.placeslikee.di

import com.example.placeslikee.data.repository.AuthRepositoryImpl
import com.example.placeslikee.data.repository.MapRepositoryImpl
import com.example.placeslikee.domain.repositories.AuthRepository
import com.example.placeslikee.domain.repositories.MapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMapRepository(impl: MapRepositoryImpl): MapRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}