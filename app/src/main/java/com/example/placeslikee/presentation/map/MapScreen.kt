package com.example.placeslikee.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.placeslikee.R
import com.example.placeslikee.domain.models.NewMarkerIfo
import com.example.placeslikee.presentation.common.CustomSnackbar
import com.example.placeslikee.presentation.markerdetails.MarkerDetailsContent
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel<MapViewModel>(),
    onNavigateToAuth: () -> Unit,
    onNavigateToCreateMarker: (NewMarkerIfo) -> Unit,
    searchQuery: String = "",
    onMarkerClick: (String) -> Unit,
    selectedMarkerId: String?,
    onMapClick: () -> Unit
) {
    val state by viewModel.mapState.collectAsState()

    val context = LocalContext.current
    val mapView = MapViewHelper()
    val isFirstTimeLoading by viewModel.isFirstTimeLoading.collectAsState()

    var userLocationLayer by remember { mutableStateOf<UserLocationLayer?>(null) }

    val placemarksMap = remember { mutableMapOf<String, PlacemarkMapObject>() }
    var previousSelectedId by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val inputListener = remember {
        object : InputListener {
            override fun onMapTap(p0: Map, p1: Point) {
                onMapClick()
            }
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
                if (p3 && isFirstTimeLoading)
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
                onMarkerClick(pointId)
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

    LaunchedEffect(searchQuery) {
        viewModel.setSearchQuery(searchQuery)
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToAuth.collect {
            onNavigateToAuth()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToCreateMarker.collect { info ->
            onNavigateToCreateMarker(info)
        }
    }

    LaunchedEffect(Unit) {
        if (!isLocationGranted) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
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
        FloatingActionButton(
            onClick = {
                if (!isLocationGranted) {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    val targetLocation = userLocationLayer?.cameraPosition()?.target

                    if (targetLocation != null) {
                        mapView.mapWindow.map.move(
                            CameraPosition(targetLocation, 17.0f, 0.0f, 0.0f),
                            Animation(Animation.Type.SMOOTH, 1f),
                            null
                        )
                    } else {
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(
                                message = "Ищем ваше местоположение...",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.medium

        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_my_location_24),
                contentDescription = "Моё местоположение"
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(bottom = 80.dp)
                .align(Alignment.BottomCenter)

        ) { snackbarData ->
            CustomSnackbar(
                snackbarData = snackbarData,
                isIconShowed = false
            )

        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }

    LaunchedEffect(state.points, mapView) {
        val mapObjects = mapView.mapWindow.map.mapObjects
        mapObjects.clear()
        placemarksMap.clear()

        val imageProvider = ImageProvider.fromResource(context, R.drawable.marker_pointer)

        state.points.forEach { point ->
            val isSelected = point.id == selectedMarkerId
            val initialScale = if (isSelected) 0.08f else 0.05f
            val placemark = mapObjects.addPlacemark(Point(point.latitude, point.longitude))
            val iconStyle = IconStyle().apply {
                anchor = PointF(0.5f, 1.0f)
                scale = initialScale
                zIndex = if (isSelected) 1f else 0f
            }
            placemark.setIcon(imageProvider, iconStyle)
            placemark.userData = point.id
            placemark.addTapListener(tapListener)

            placemarksMap[point.id] = placemark

        }
    }

    LaunchedEffect(Unit) {
        viewModel.cameraCommands.collect { command ->
            val map = mapView.mapWindow.map

            when (command) {
                is CameraCommand.MoveTo -> {
                    val targetPoint = Point(command.lat, command.lon)
                    val finalZoom = maxOf(map.cameraPosition.zoom, command.zoom)

                    if(command.animate) {
                        map.move(
                            CameraPosition(targetPoint, finalZoom, 0.0f, 0.0f),
                            Animation(Animation.Type.SMOOTH, 0.5f),
                            null
                        )
                    }
                    else{
                        map.move( CameraPosition(targetPoint, finalZoom, 0.0f, 0.0f))
                    }
                }

                is CameraCommand.FitBounds -> {
                    val points = command.points
                    if (points.isEmpty()) return@collect
                    if (points.size == 1) {
                        val point = points.first()
                        map.move(
                            CameraPosition(Point(point.latitude, point.longitude), 16.0f, 0.0f, 0.0f),
                            Animation(Animation.Type.SMOOTH, 1f),
                            null
                        )
                    } else {
                        val minLat = points.minBy { it.latitude }.latitude
                        val maxLat = points.maxBy { it.latitude }.latitude
                        val minLon = points.minBy { it.longitude }.longitude
                        val maxLon = points.maxBy { it.longitude }.longitude

                        if (minLat == maxLat && minLon == maxLon) {
                            map.move(
                                CameraPosition(Point(minLat, minLon), 16.0f, 0.0f, 0.0f),
                                Animation(Animation.Type.SMOOTH, 1f),
                                null
                            )
                            return@collect
                        }

                        val boundingBox = BoundingBox(Point(minLat, minLon), Point(maxLat, maxLon))
                        var cameraPos = map.cameraPosition(Geometry.fromBoundingBox(boundingBox))

                        val paddedZoom = (cameraPos.zoom - 0.5f).coerceAtLeast(0f)
                        cameraPos = CameraPosition(cameraPos.target, paddedZoom, cameraPos.azimuth, cameraPos.tilt)

                        map.move(
                            cameraPos,
                            Animation(Animation.Type.SMOOTH, 1.2f),
                            null
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedMarkerId) {
        val newSelectedId = selectedMarkerId
        previousSelectedId?.let { oldId ->
            if (oldId != newSelectedId) {
                placemarksMap[oldId]?.let { placemark ->
                    launch {
                        placemark.zIndex = 0f
                        val scaleAnim = Animatable(0.08f)
                        scaleAnim.animateTo(
                            targetValue = 0.05f,
                            animationSpec = tween(durationMillis = 300)
                        ) {
                            placemark.setIconStyle(IconStyle().apply {
                                anchor = PointF(0.5f, 1.0f)
                                scale = this@animateTo.value
                            })
                        }
                    }
                }
            }
        }
        newSelectedId?.let {newId ->
            placemarksMap[newId]?.let {placemark ->
                launch{
                    placemark.zIndex = 1f
                    val scaleAnim = Animatable(0.05f)
                    scaleAnim.animateTo(
                        targetValue = 0.08f,
                        animationSpec =  tween(durationMillis = 300)
                    ){
                        placemark.setIconStyle(IconStyle().apply{
                            anchor = PointF(0.5f, 1.0f)
                            scale = this@animateTo.value
                        })
                    }
                }
            }

        }
        previousSelectedId = newSelectedId
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.mapWindow.map.removeInputListener(inputListener)
            mapView.mapWindow.map.removeCameraListener(cameraListener)
        }
    }

}