package com.ghostcat.deadzone

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ghostcat.deadzone.views.HomePage
import com.ghostcat.deadzone.views.TestingPage

@Composable
fun AppNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable(Screen.Home.route) { HomePage(navController, modifier) }
        composable(Screen.Testing.route) { TestingPage(navController, modifier) }
    }

}


//Destinations
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Testing : Screen("testing")
}