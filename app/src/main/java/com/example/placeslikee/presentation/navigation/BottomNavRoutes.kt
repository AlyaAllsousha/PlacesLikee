package com.example.placeslikee.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem (
    val route: String,
    val title: String,
    val icon: ImageVector,
    val iconSelected: ImageVector
){
    data object Map : BottomNavItem(
        route = "map",
        title = "Карта",
        icon = Icons.Outlined.Place,
        iconSelected = Icons.Filled.Place
    )
    data object Notification : BottomNavItem(
        route = "notification",
        title = "Уведомления",
        icon = Icons.Outlined.Notifications,
        iconSelected = Icons.Filled.Notifications
    )
    data object Profile : BottomNavItem(
        route = "profile",
        title = "Профиль",
        icon = Icons.Outlined.Person,
        iconSelected = Icons.Filled.Person
    )
}
val bottomNavItem = listOf(
    BottomNavItem.Map,
    BottomNavItem.Profile,
    BottomNavItem.Notification
)