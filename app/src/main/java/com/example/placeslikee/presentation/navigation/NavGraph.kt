package com.example.placeslikee.presentation.navigation

sealed class Screen(val route: String) {
    data object Map : Screen("map")
    data object Notification: Screen("notification")
    data object Profile: Screen("profile")

    data object MarkDetail : Screen("mark_detail/{markId}"){
        fun createRoute(markId: String) = "mark_detail/$markId"
    }
    data object EditMark : Screen("edit_mark/{markId}"){
        fun createRoute(markId: String) = "edit_mark/$markId"
    }
}