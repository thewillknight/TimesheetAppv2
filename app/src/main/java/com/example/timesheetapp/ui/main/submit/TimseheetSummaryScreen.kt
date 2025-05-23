package com.example.timesheetapp.ui.main.submit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.timesheetapp.data.model.TimesheetEntry
import com.example.timesheetapp.viewmodel.TimesheetViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimesheetSummaryScreen(
    weekStart: String,
    navController: NavController,
    viewModel: TimesheetViewModel = viewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val timesheets by viewModel.timesheets.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val subcategories by viewModel.subcategories.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val projectMap = remember(projects) { projects.associateBy { it.id } }
    val subcategoryMap = remember(subcategories) { subcategories.associateBy { it.code } }

    val timesheet = remember(timesheets, weekStart) {
        timesheets.find { it.weekStart == weekStart }
    }

    val isEditable = remember(timesheet) {
        timesheet?.status?.lowercase(Locale.ROOT) in listOf("draft", "rejected")
    }

    val totalHours = entries.sumOf { it.dailyHours.sum() }

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<TimesheetEntry?>(null) }

    LaunchedEffect(weekStart) {
        viewModel.loadTimesheets()
        viewModel.loadEntries(weekStart)
        viewModel.loadProjects()
        viewModel.loadSubcategories()
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedEntry = null
            }
        ) {
            EntryDetailBottomSheet(
                weekStart = weekStart,
                existingEntry = selectedEntry,
                isEditable = isEditable,
                onDismiss = {
                    showBottomSheet = false
                    selectedEntry = null
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isEditable) {
                FloatingActionButton(onClick = {
                    selectedEntry = null
                    showBottomSheet = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Entry")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Week: $weekStart", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))

            val statusDisplay = timesheet?.status?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } ?: "Loading..."

            val statusColor = when (timesheet?.status?.lowercase(Locale.ROOT)) {
                "draft", "rejected" -> Color.Red
                "pending" -> Color(0xFFFFA000)
                "approved" -> Color(0xFF388E3C)
                else -> Color.Gray
            }

            Text("Status: $statusDisplay", color = statusColor)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Total Hours: $totalHours", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (isEditable) {
                Button(
                    onClick = {
                        viewModel.submitTimesheet(weekStart)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Timesheet submitted for approval")
                        }
                    },
                    enabled = totalHours >= 37.5,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Timesheet")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Entries", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (entries.isEmpty()) {
                Text("No entries added yet.")
            } else {
                LazyColumn {
                    items(entries) { entry ->
                        EntrySummaryItem(
                            entry = entry,
                            isEditable = isEditable || !isEditable, // allow all to view
                            projectName = projectMap[entry.projectId]?.name,
                            subcategoryDesc = subcategoryMap[entry.subcategoryId]?.description
                        ) {
                            selectedEntry = entry
                            showBottomSheet = true
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EntrySummaryItem(
    entry: TimesheetEntry,
    isEditable: Boolean,
    projectName: String?,
    subcategoryDesc: String?,
    onClick: () -> Unit
) {
    val totalHours = entry.dailyHours.sum()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEditable || !isEditable, onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Project: ${entry.projectId} - ${projectName ?: "Unknown"}")
            Text("Subcategory: ${entry.subcategoryId} - ${subcategoryDesc ?: "Unknown"}")
            Text("Total Hours: $totalHours")
            Text("Approved: ${if (entry.approved) "✅" else "❌"}")
        }
    }
}
