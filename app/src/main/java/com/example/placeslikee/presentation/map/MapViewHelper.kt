package com.example.placeslikee.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView

@Composable
fun MapViewHelper(): MapView{
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember {
        MapView(context)
    }


    DisposableEffect(lifecycleOwner){
       val observer = LifecycleEventObserver{_, event ->
           when(event){
               Lifecycle.Event.ON_START ->{
                   MapKitFactory.getInstance().onStart()
                   mapView.onStart()
               }
               Lifecycle.Event.ON_STOP -> {
                   mapView.onStop()
                   MapKitFactory.getInstance().onStop()
               }

               else -> {}
           }
       }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
    return  mapView
}