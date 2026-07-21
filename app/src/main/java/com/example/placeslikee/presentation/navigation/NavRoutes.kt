package com.example.placeslikee.presentation.navigation

sealed class NavRoutes(val routes: String) {
    object Profile : NavRoutes("profile")
    object Main : NavRoutes("main")
    object List : NavRoutes("list")
    object Auth : NavRoutes("auth")
}