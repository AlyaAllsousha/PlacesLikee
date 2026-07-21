package com.example.placeslikee.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.placeslikee.R
import com.example.placeslikee.presentation.map.details.MarkerDetailsContent
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel<MapViewModel>(),
    onNavigateToAuth: () -> Unit
) {
    val state by viewModel.mapState.collectAsState()
    val context = LocalContext.current
    val mapView = MapViewHelper()

    val isFirstTimeLoading by viewModel.isFirstTimeLoading.collectAsState()

    //saving the selected marker for showing its details
    val selectedMarker by viewModel.selectedMarker.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var userLocationLayer by remember { mutableStateOf<UserLocationLayer?>(null) }

    val inputListener = remember {
        object : InputListener {
            override fun onMapTap(p0: Map, p1: Point) {}
            override fun onMapLongTap(p0: Map, p1: Point) {
                viewModel.onMapClick(MapEvent.OnMapLongClick(p1.latitude, p1.longitude))
            }
        }
    }

    val cameraListener = remember {
        object : CameraListener {
            override fun onCameraPositionChanged(
                p0: Map,
                p1: CameraPosition,
                p2: CameraUpdateReason,
                p3: Boolean
            ) {
                viewModel.updateCameraPosition(p1)
                if(p3 && isFirstTimeLoading)
                    viewModel.setIsFirstTimeLoading(false)
            }

        }
    }

    //Permission for location checking
    var isLocationGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
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
        object : UserLocationObjectListener {
            override fun onObjectAdded(p0: UserLocationView) {}

            override fun onObjectRemoved(p0: UserLocationView) {}

            override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
                if (isFirstTimeLoading && viewModel.getLatestCameraPosition() == null) {
                    val userPoint = p0.arrow.geometry
                    if (userPoint.latitude == 0.0 && userPoint.longitude == 0.0)
                        return
                    mapView.mapWindow.map.move(
                        CameraPosition(userPoint, 17.0f, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 1f),
                        null
                    )
                    viewModel.setIsFirstTimeLoading(false)
                }
            }

        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToAuth.collect {
            onNavigateToAuth()
        }
        if (!isLocationGranted) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            factory = {
                mapView.apply {
                    val map = mapWindow.map
                    map.addInputListener(inputListener)
                    map.addCameraListener(cameraListener)
                    viewModel.getLatestCameraPosition()?.let { savedPos ->
                        map.move(savedPos)

                    }
                }
            },
            update = { view ->
                val hasFineLocation = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val hasCoarseLocation = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (isLocationGranted && (hasFineLocation || hasCoarseLocation)) {
                    try {
                        if (userLocationLayer == null) {
                            val kit = MapKitFactory.getInstance()
                            val layer = kit.createUserLocationLayer(view.mapWindow)

                            layer.setObjectListener(userLocationObjectListener)
                            layer.isVisible = true

                            userLocationLayer = layer
                        }

                    } catch (e: SecurityException) {
                        Log.e("my log", "MapScreen: ${e.message}")
                    }
                }
                userLocationLayer?.isVisible = isLocationGranted

            },
            modifier = Modifier.fillMaxSize(),


            )
    }
    LaunchedEffect(state.points, mapView) {
        val mapObjects = mapView.mapWindow.map.mapObjects
        mapObjects.clear()
        val imageProvider = ImageProvider.fromResource(context, R.drawable.marker_pointer)
        val iconStyle = IconStyle().apply {
            anchor = PointF(0.5f, 1.0f)
            scale = 0.07f
        }
        state.points.forEach { point ->
            val placemark = mapObjects.addPlacemark(Point(point.lat, point.longitude))
            placemark.setIcon(imageProvider, iconStyle)

            placemark.userData = point.id
            placemark.addTapListener(tapListener)
        }
    }
    selectedMarker?.let {marker ->
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.dismissMarkerDetails()
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MarkerDetailsContent(marker)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            mapView.mapWindow.map.removeInputListener(inputListener)
            mapView.mapWindow.map.removeCameraListener(cameraListener)
        }
    }

}