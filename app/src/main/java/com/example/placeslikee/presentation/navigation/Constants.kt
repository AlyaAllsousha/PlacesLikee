package com.example.placeslikee.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Profile",
            icon = Icons.Filled.Person,
            route = "profile"
        ),
        BottomNavItem(
            label = "Map",
            icon = Icons.Filled.Place,
            route = "map"
        ),
        BottomNavItem(
            label = "list",
            icon = Icons.AutoMirrored.Filled.List,
            route = "list"
        )
    )
}
