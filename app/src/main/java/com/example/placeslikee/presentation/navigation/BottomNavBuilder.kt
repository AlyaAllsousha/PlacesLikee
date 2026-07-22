package com.example.placeslikee.presentation.navigation


import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.placeslikee.presentation.main.MainScreen
import com.example.placeslikee.presentation.authentication.AuthScreen
import com.example.placeslikee.presentation.list.ListScreen
import com.example.placeslikee.presentation.newmarker.CreateMarkerScreen
import com.example.placeslikee.presentation.profile.ProfileScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Main.routes,
        modifier = Modifier.padding(paddingValues = padding).consumeWindowInsets(padding),
        enterTransition = {
            fadeIn(animationSpec = tween(250))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(250))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(250))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(250))
        },
        builder = {
            composable(
                route = NavRoutes.Main.routes
            ) {
                MainScreen(
                    onNavigateToAuth = {
                        navController.navigate(NavRoutes.Auth.routes)
                    },
                    onNavigateToProfile = {
                        navController.navigate(NavRoutes.Profile.routes)
                    },
                    onNavigateToCreateMarker = {info ->
                        navController.navigate("${NavRoutes.CreateMark.routes}/${info.lat}/${info.lon}")
                    }
                )
            }
            composable(NavRoutes.Auth.routes){
                AuthScreen(
                    onNavigateToMap = {
                        navController.popBackStack()
                    }
                )
            }
            composable(NavRoutes.Profile.routes) {
                ProfileScreen()
            }
            composable(NavRoutes.List.routes){
                ListScreen()
            }
            composable(
                route = "${NavRoutes.CreateMark.routes}/{lat}/{lon}",
                arguments = listOf(
                    navArgument("lat"){type = NavType.StringType},
                    navArgument("lon"){type = NavType.StringType}
                )
            ) {
                CreateMarkerScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
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
        val scope = rememberCoroutineScope()
        Constants.BottomNavItems.forEach { navItem ->
                NavigationBarItem(
                    selected = currentRoute == navItem.route,
                    onClick = {
                        if(navItem.route == NavRoutes.Main.routes){
                            navController.popBackStack()
                        }

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