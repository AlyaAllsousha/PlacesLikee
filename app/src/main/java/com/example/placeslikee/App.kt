package com.example.placeslikee

import android.app.Application
import android.util.Log
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App :Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
//        Log.d("my log", "onCreate App: key = ${BuildConfig.MAPKIT_API_KEY}")
    }

}