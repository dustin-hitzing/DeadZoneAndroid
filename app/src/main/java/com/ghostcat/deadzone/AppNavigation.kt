package com.ghostcat.deadzone

import PermissionHandler
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ghostcat.deadzone.views.HomePage
import com.ghostcat.deadzone.views.MapListPage
import com.ghostcat.deadzone.views.MapPage
import com.ghostcat.deadzone.views.TestingPage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(modifier: Modifier) {
    PermissionHandler(
        onAllPermissionsGranted = {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "home") {
                composable(Screen.Home.route) { HomePage(navController, modifier) }
                composable(Screen.Testing.route) { TestingPage(navController, modifier) }
                composable(Screen.MapList.route) { MapListPage(navController, modifier) }
                composable(Screen.Map.route) { MapPage(navController, modifier) }
            }
        },
        onSomePermissionsDenied = {
            Text("The following permissions were denied: $it")
        }
    )
}


//Destinations
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Testing : Screen("testing")
    object MapList : Screen("mapList")
    object Map : Screen("map")
}