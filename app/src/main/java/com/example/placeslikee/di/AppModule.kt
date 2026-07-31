package com.example.placeslikee.di

import com.example.placeslikee.workmanger.MarkersSyncManager
import com.example.placeslikee.data.local.LocalDB
import com.example.placeslikee.data.remote.RemoteDB
import com.example.placeslikee.data.repository.AuthRepositoryImpl
import com.example.placeslikee.data.repository.MapRepositoryImpl
import com.example.placeslikee.domain.repositories.AuthRepository

import com.example.placeslikee.domain.repositories.MapRepository

import com.google.firebase.auth.FirebaseAuth


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
    fun provideFirebaseAuth(): FirebaseAuth  = FirebaseAuth.getInstance()

}