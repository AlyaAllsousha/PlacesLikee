package com.example.placeslikee.di

import com.example.placeslikee.data.remote.notifications.VercelApi
import com.google.firebase.auth.FirebaseAuth


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth  = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://placeslikee.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideVercelApi(retrofit: Retrofit) : VercelApi{
        return retrofit.create(VercelApi::class.java)
    }
}
