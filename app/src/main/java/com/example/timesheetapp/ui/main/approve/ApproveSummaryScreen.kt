package com.example.timesheetapp.ui.main.approve

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.viewmodel.ApproveViewModel
import com.example.timesheetapp.data.model.TimesheetEntry
import kotlinx.coroutines.launch

@Composable
fun ApproveSummaryScreen(
    submitterId: String,
    weekStart: String,
    viewModel: ApproveViewModel = viewModel()
) {
    val entries by viewModel.selectedTimesheetEntries.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadTimesheetEntries(submitterId, weekStart)
    }

    val totalHours = entries.sumOf { it.dailyHours.sum() }
    val allApproved = entries.all { it.approved }
    val anyUnapproved = entries.any { !it.approved }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Week of $weekStart", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Hours: $totalHours")
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(entries) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Project: ${entry.projectId}")
                            Text("Subcategory: ${entry.subcategoryId}")
                            Text("Hours: ${entry.dailyHours.joinToString()}")
                            Text("Approved: ${if (entry.approved) "✅" else "❌"}")
                            if (!entry.approved) {
                                Button(
                                    onClick = {
                                        viewModel.approveEntry(submitterId, weekStart, entry.id)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Entry approved")
                                        }
                                    },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Approve Entry")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (allApproved) {
                Button(
                    onClick = {
                        viewModel.approveTimesheet(submitterId, weekStart)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Timesheet approved")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Approve Full Timesheet")
                }
            } else if (anyUnapproved) {
                Button(
                    onClick = {
                        viewModel.rejectTimesheet(submitterId, weekStart)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Timesheet rejected")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reject Timesheet")
                }
            }
        }
    }
}
