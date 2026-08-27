package com.example.placeslikee.presentation.navigation

sealed class NavRoutes(val routes: String) {
    object Profile : NavRoutes("profile")
    object Main : NavRoutes("main")
    object Auth : NavRoutes("auth")
    object Favourite : NavRoutes("favourite")
    object EditMarker : NavRoutes("edit_marker")
    object CreateMark : NavRoutes("create_mark")
    object MarkerDetails: NavRoutes("merker_details")
}