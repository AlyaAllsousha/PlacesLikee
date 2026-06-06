package com.example.placeslikee.presentation.navigation

sealed class NavRoutes(val routes: String) {
    object Profile : NavRoutes("profile")
    object Map : NavRoutes("map")
    object List : NavRoutes("list")
}