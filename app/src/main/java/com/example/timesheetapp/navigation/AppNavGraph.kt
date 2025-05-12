package com.example.timesheetapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.timesheetapp.ui.auth.LoginScreen
import com.example.timesheetapp.ui.auth.SignUpScreen
import com.example.timesheetapp.ui.main.MainScreen
import com.example.timesheetapp.ui.main.submit.TimesheetSummaryScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Main : Screen("main")

}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(Screen.Main.route) {
            MainScreen(navController)
        }
    }
}
