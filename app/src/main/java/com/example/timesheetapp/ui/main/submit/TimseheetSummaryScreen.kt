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

    val timesheet = timesheets.find { it.weekStart == weekStart }
    val isDraft = timesheet?.status?.lowercase(Locale.ROOT) == "draft"

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<TimesheetEntry?>(null) }

    LaunchedEffect(weekStart) {
        viewModel.loadEntries(weekStart)
        viewModel.loadTimesheets()
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedEntry = null
                viewModel.loadEntries(weekStart)
            }
        ) {
            EntryDetailBottomSheet(
                weekStart = weekStart,
                existingEntry = selectedEntry,
                isEditable = isDraft,
                onDismiss = {
                    showBottomSheet = false
                    selectedEntry = null
                }
            )
        }
    }

    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            if (isDraft) {
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

            Text(
                text = "Status: ${timesheet?.status?.replaceFirstChar { it.titlecase(Locale.getDefault()) } ?: "Unknown"}",
                color = when (timesheet?.status?.lowercase(Locale.ROOT)) {
                    "draft" -> Color.Red
                    "pending" -> Color(0xFFFFA000)
                    "approved" -> Color(0xFF388E3C)
                    else -> Color.Gray
                }
            )

            val totalHours = entries.sumOf { it.dailyHours.sum() }

            Text(
                text = "Total Hours: ${"%.1f".format(totalHours)}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (isDraft && entries.isNotEmpty() && totalHours >= 37.5) {
                Button(
                    onClick = {
                        viewModel.submitTimesheet(weekStart)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Timesheet")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Entries", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (entries.isEmpty()) {
                Text("No entries added yet.")
            } else {
                LazyColumn {
                    items(entries) { entry ->
                        EntrySummaryItem(entry = entry) {
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
fun EntrySummaryItem(entry: TimesheetEntry, onClick: () -> Unit) {
    val totalHours = entry.dailyHours.sum()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Project: ${entry.projectId}")
            Text("Subcategory: ${entry.subcategoryId}")
            Text("Total Hours: $totalHours")
            Text("Approved: ${if (entry.approved) "✅" else "❌"}")
        }
    }
}
