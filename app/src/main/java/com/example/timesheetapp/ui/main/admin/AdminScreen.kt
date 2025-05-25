package com.example.timesheetapp.ui.main.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timesheetapp.R
import com.example.timesheetapp.ui.main.admin.sections.DelegationSection
import com.example.timesheetapp.ui.main.admin.sections.ProjectSection
import com.example.timesheetapp.ui.main.admin.sections.SubcategorySection

@Composable
fun AdminScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabDelegation = stringResource(id = R.string.tab_delegation)
    val tabProjects = stringResource(id = R.string.tab_projects)
    val tabSubcategories = stringResource(id = R.string.tab_subcategories)

    val tabTitles = listOf(tabDelegation, tabProjects, tabSubcategories)

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
