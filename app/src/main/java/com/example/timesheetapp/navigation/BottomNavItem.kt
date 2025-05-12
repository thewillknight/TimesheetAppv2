package com.example.timesheetapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    object Submit : BottomNavItem("Submit", "submit", Icons.Filled.Check)
    object Approve : BottomNavItem("Approve", "approve", Icons.Filled.ThumbUp)
    object Admin : BottomNavItem("Admin", "admin", Icons.Filled.Settings)
}
