package com.example.timesheetapp.ui.main.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timesheetapp.ui.main.admin.sections.DelegationSection
import com.example.timesheetapp.ui.main.admin.sections.ProjectSection
import com.example.timesheetapp.ui.main.admin.sections.SubcategorySection

@Composable
fun AdminScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabTitles = listOf("Delegation", "Projects", "Subcategories")

    Column(modifier = Modifier.fillMaxSize()) {
        // Top tabs
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section content
        when (selectedTabIndex) {
            0 -> DelegationSection()
            1 -> ProjectSection()
            2 -> SubcategorySection()
        }
    }
}
