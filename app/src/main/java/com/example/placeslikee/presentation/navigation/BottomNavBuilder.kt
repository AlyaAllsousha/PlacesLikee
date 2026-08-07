package com.example.placeslikee.presentation.navigation


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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.placeslikee.presentation.main.MainScreen
import com.example.placeslikee.presentation.authentication.AuthScreen
import com.example.placeslikee.presentation.favourite.FavouriteScreen
import com.example.placeslikee.presentation.list.ListScreen
import com.example.placeslikee.presentation.newmarker.CreateMarkerScreen
import com.example.placeslikee.presentation.profile.ProfileScreen
import com.example.placeslikee.presentation.profile.editMarker.EditMarkerScreen

@Composable
fun NavHostContainer(
    navController: NavHostController, padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Main.routes,
        modifier = Modifier
            .padding(paddingValues = padding)
            .consumeWindowInsets(padding),
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
                MainScreen(onNavigateToAuth = {
                    navController.navigate(NavRoutes.Auth.routes)
                }, onNavigateToProfile = {
                    navController.navigate(NavRoutes.Profile.routes)
                }, onNavigateToCreateMarker = { info ->
                    navController.navigate("${NavRoutes.CreateMark.routes}/${info.lat}/${info.lon}")
                })
            }
            composable(NavRoutes.Auth.routes) {
                AuthScreen(
                    onNavigateToMap = {
                        navController.navigate(NavRoutes.Main.routes)
                    })
            }
            composable(NavRoutes.Profile.routes) { backStackEntry ->
                val savedStateHandle = backStackEntry.savedStateHandle
                val externalSnackbarMessage by savedStateHandle
                    .getStateFlow<String?>("edit_message", null)
                    .collectAsState()
                ProfileScreen(
                    externalSnackbarMessage = externalSnackbarMessage,
                    onClearSnackbarMessage = {
                        savedStateHandle.remove<String>("snackbar_message")
                    },
                    onNavigateToAuth = { navController.navigate(NavRoutes.Auth.routes) },
                    onNavigateToEdit = {markerId ->
                        navController.navigate("${NavRoutes.EditMarker.routes}/$markerId")
                    }
                )
            }
            composable(NavRoutes.List.routes) {
                ListScreen()
            }
            composable(
                route = "${NavRoutes.CreateMark.routes}/{lat}/{lon}",
                arguments = listOf(
                    navArgument("lat") { type = NavType.StringType },
                    navArgument("lon") { type = NavType.StringType })
            ) {
                CreateMarkerScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    })
            }
            composable (
                route = NavRoutes.Favourite.routes
            ){
                FavouriteScreen(
                    onNavigateToAuth = {
                           navController.navigate(NavRoutes.Auth.routes) 
                    }
                )
            }
            composable (
                route = "${NavRoutes.EditMarker.routes}/{markerId}",
                arguments = listOf(
                    navArgument("markerId") { type = NavType.StringType }
                )

            ){
                EditMarkerScreen(
                    onNavigateBack = {message ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("edit_message", message)
                        navController.popBackStack()
                    }
                )
            }

        })
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        val scope = rememberCoroutineScope()
        Constants.BottomNavItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route, onClick = {
                    if (navItem.route == NavRoutes.Main.routes) {
                        navController.popBackStack()
                    }

                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }, icon = {
                    Icon(
                        imageVector = navItem.icon, contentDescription = navItem.label
                    )
                }, label = {
                    Text(text = navItem.label)
                }, alwaysShowLabel = false, colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )

        }

    }
}