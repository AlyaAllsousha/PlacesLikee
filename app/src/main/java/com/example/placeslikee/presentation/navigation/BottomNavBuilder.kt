package com.example.placeslikee.presentation.navigation


import androidx.compose.runtime.getValue


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.placeslikee.presentation.MainScreen
import com.example.placeslikee.presentation.list.ListScreen
import com.example.placeslikee.presentation.map.MapScreen
import com.example.placeslikee.presentation.profile.ProfileScreen
import kotlinx.coroutines.selects.select

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "map",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable("map") {
                MainScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("list"){
                ListScreen()
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar (
        containerColor = MaterialTheme.colorScheme.background
    ){
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        Constants.BottomNavItems.forEach { navItem ->
            if (navItem.label != "list") {
                NavigationBarItem(
                    selected = currentRoute == navItem.route,
                    onClick = {
                        navController.navigate(navItem.route){
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = navItem.label
                        )
                    },
                    label = {
                        Text(text = navItem.label)
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )

            }
        }

    }
}