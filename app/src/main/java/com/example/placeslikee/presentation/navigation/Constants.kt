package com.example.placeslikee.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Профиль",
            icon = Icons.Filled.Person,
            route = "profile"
        ),
        BottomNavItem(
            label = "Карта",
            icon = Icons.Filled.Place,
            route = "main"
        ),
        BottomNavItem(
            label = "Избранное",
            icon = Icons.Filled.Favorite,
            route = "favourite"
        )
    )
}
