package com.example.placeslikee.di

import com.example.placeslikee.data.repository.AuthRepositoryImpl
import com.example.placeslikee.data.repository.ImageStorageRepositoryImpl
import com.example.placeslikee.data.repository.LikeRepositoryImpl
import com.example.placeslikee.data.repository.MarkRepositoryImpl
import com.example.placeslikee.data.repository.SubscriptionsRepositoryImpl
import com.example.placeslikee.domain.repositories.AuthRepository
import com.example.placeslikee.domain.repositories.ImageStorageRepository
import com.example.placeslikee.domain.repositories.LikeRepository
import com.example.placeslikee.domain.repositories.MarkRepository
import com.example.placeslikee.domain.repositories.SubscriptionsRepository
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
    abstract fun bindMapRepository(impl: MarkRepositoryImpl): MarkRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLikeRepository(impl: LikeRepositoryImpl): LikeRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(impl: ImageStorageRepositoryImpl) : ImageStorageRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: SubscriptionsRepositoryImpl) : SubscriptionsRepository
}