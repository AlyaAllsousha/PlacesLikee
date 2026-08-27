package com.example.placeslikee.presentation

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.placeslikee.ui.theme.PlacesLikeeTheme

import com.example.placeslikee.presentation.navigation.BottomNavigationBar
import com.example.placeslikee.presentation.navigation.NavHostContainer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            navController = navHostController

            PlacesLikeeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController = navHostController) },
                    content = { padding ->
                        NavHostContainer(navController = navHostController, padding = padding) }
                )
            }
        }
        intent?.data?.lastPathSegment?.let { markerId ->
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(markerId.hashCode())
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.handleDeepLink(intent)
        val markerId = intent.data?.lastPathSegment
        if (markerId != null) {
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(markerId.hashCode())
        }
    }
}


