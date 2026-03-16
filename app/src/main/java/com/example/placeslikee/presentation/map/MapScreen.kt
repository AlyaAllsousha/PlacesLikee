package com.example.placeslikee.presentation.map

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.placeslikee.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel<MapViewModel>()
) {
    val state by viewModel.mapState.collectAsState()
    val context = LocalContext.current
    val mapView = MapViewHelper()
    var followUser by remember { mutableStateOf(true) }
    var userLocationLayer by remember { mutableStateOf<UserLocationLayer?>(null) }

    val inputListener = remember {
        object : InputListener {
            override fun onMapTap(p0: Map, p1: Point) {}

            override fun onMapLongTap(p0: Map, p1: Point) {
                viewModel.onMapClick(MapEvent.OnMapLongClick(p1.latitude, p1.longitude))
            }
        }
    }
    //Permission for location checking
    var isLocationGranted by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }


    val tapListener = remember {
        MapObjectTapListener { mapObject, point ->
            val pointId = mapObject.userData as? String
            if (pointId != null) {
                viewModel.onMapClick(MapEvent.onPointClick(pointId))
            }
            true
        }
    }

    val userLocationObjectListener = remember {
        object : UserLocationObjectListener{
            override fun onObjectAdded(p0: UserLocationView) {}

            override fun onObjectRemoved(p0: UserLocationView) {}

            override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent){
                if(followUser){
                    val userPoint = p0.arrow.geometry
                    if (userPoint.latitude == 0.0 && userPoint.longitude == 0.0)
                        return
                    mapView.map.move(
                        CameraPosition(userPoint, 17.0f, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 1f),
                        null
                    )

                        followUser = false

                }
            }

        }
    }
    LaunchedEffect(Unit) {
        if (!isLocationGranted) {
            launcher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            factory = {
                mapView.apply {
                    mapWindow.map.addInputListener(inputListener)
                }
            },
            update = { view ->
                val hasFineLocation = androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                val hasCoarseLocation = androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (isLocationGranted && (hasFineLocation || hasCoarseLocation)) {
                    try {
                        if (userLocationLayer == null) {
                            val kit = MapKitFactory.getInstance()
                            val layer = kit.createUserLocationLayer(view.mapWindow)

                            layer.setObjectListener(userLocationObjectListener)
                            layer.isVisible = true

                            userLocationLayer = layer
                        }
                        Log.d("my log", "MapScreen: works correctly")

                    }
                    catch (e: SecurityException){
                        Log.e("my log", "MapScreen: ${e.message}")}
                }
                    userLocationLayer?.isVisible = isLocationGranted

                },
            modifier = Modifier.fillMaxSize(),

        )
    }
    LaunchedEffect(state.points) {
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()
        state.points.forEach { point ->
            val placemark = mapObjects.addPlacemark(Point(point.latitude, point.longtitude))
            placemark.setIcon(ImageProvider.fromResource(context, R.drawable.place_marker))
            placemark.userData = point.id
            placemark.addTapListener(tapListener)
        }
    }

}