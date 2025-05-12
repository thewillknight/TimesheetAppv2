package com.example.timesheetapp.ui.main

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timesheetapp.ui.components.AppTopBar
import com.google.firebase.auth.FirebaseAuth
import com.example.timesheetapp.navigation.BottomNavItem
import com.example.timesheetapp.ui.main.admin.AdminScreen
import com.example.timesheetapp.ui.main.approve.ApproveScreen
import com.example.timesheetapp.ui.main.submit.SubmitScreen
import com.example.timesheetapp.ui.main.submit.TimesheetSummaryScreen
import com.example.timesheetapp.viewmodel.UserViewModel
import java.util.Locale


@Composable
fun MainScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val user by userViewModel.user.collectAsState()
    val isApprover by userViewModel.isApprover.collectAsState()
    val navHostController = rememberNavController()
    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(Unit) {
        userViewModel.loadUserData()
        userViewModel.checkIfUserIsApprover()
    }

    val tabs = buildList {
        add(BottomNavItem.Submit)
        if (isApprover) add(BottomNavItem.Approve)
        if (user?.admin == true) add(BottomNavItem.Admin)
    }


    Scaffold(
        topBar = {
            AppTopBar(title = currentRoute?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
                ?: "Timesheet", navController = navController)
        },
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = tab.route == currentRoute,
                        onClick = {
                            navHostController.navigate(tab.route) {
                                popUpTo(navHostController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(tab.title) },
                        icon = { Icon(tab.icon, contentDescription = null) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = navHostController, startDestination = "submit", Modifier.padding(padding)) {
            composable("submit") { SubmitScreen(navController = navHostController) }
            composable("approve") { ApproveScreen(navController) }
            composable("admin") { AdminScreen(navController) }
            composable("timesheetSummary/{weekStart}") { backStackEntry ->
                val weekStart = backStackEntry.arguments?.getString("weekStart") ?: return@composable
                TimesheetSummaryScreen(weekStart = weekStart, navController = navHostController)
            }
        }
    }
}
