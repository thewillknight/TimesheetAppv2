package com.example.timesheetapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.timesheetapp.ui.auth.LoginScreen
import com.example.timesheetapp.ui.auth.SignUpScreen
import com.example.timesheetapp.ui.main.MainScreen
import com.example.timesheetapp.ui.main.approve.ApproveSummaryScreen
import com.example.timesheetapp.ui.main.approve.SubmitterTimesheetsScreen
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
       // composable("approveTimesheets/{submitterId}") { backStackEntry ->
       //     val submitterId = backStackEntry.arguments?.getString("submitterId") ?: return@composable
       //     SubmitterTimesheetsScreen(submitterId, navController)
       // }
        composable("submitterTimesheets/{submitterId}/{submitterName}") { backStackEntry ->
            val submitterId = backStackEntry.arguments?.getString("submitterId") ?: return@composable
            val submitterName = backStackEntry.arguments?.getString("submitterName") ?: "Unknown"
            SubmitterTimesheetsScreen(navController, submitterId, submitterName)
        }
        composable("approveSummary/{submitterId}/{weekStart}") { backStackEntry ->
            val submitterId = backStackEntry.arguments?.getString("submitterId") ?: return@composable
            val weekStart = backStackEntry.arguments?.getString("weekStart") ?: return@composable
            ApproveSummaryScreen(submitterId = submitterId, weekStart = weekStart)
        }
    }
}
