package com.example.timesheetapp.ui.main.approve

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.R
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

    val weekOfLabel = stringResource(id = R.string.week_of, weekStart)
    val totalHoursLabel = stringResource(id = R.string.total_hours, entries.sumOf { it.dailyHours.sum() })
    val approveFullTimesheetLabel = stringResource(id = R.string.approve_full_timesheet)
    val rejectTimesheetLabel = stringResource(id = R.string.reject_timesheet)
    val timesheetApprovedMsg = stringResource(id = R.string.timesheet_approved_snackbar)
    val timesheetRejectedMsg = stringResource(id = R.string.timesheet_rejected_snackbar)
    //val projectLabel = stringResource(id = R.string.project_label_inline, "")
    //val subcategoryLabel = stringResource(id = R.string.subcategory_label_inline, "")
    //val hoursLabel = stringResource(id = R.string.hours_label_inline, "")
    //val approvedLabel = stringResource(id = R.string.approved_label, "")
    val approveEntryLabel = stringResource(id = R.string.approve_entry)
    val entryApprovedMsg = stringResource(id = R.string.entry_approved_snackbar)

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
            Text(weekOfLabel, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(totalHoursLabel)
            Spacer(modifier = Modifier.height(16.dp))

            if (allApproved) {
                Button(
                    onClick = {
                        viewModel.approveTimesheet(submitterId, weekStart)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(timesheetApprovedMsg)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(approveFullTimesheetLabel)
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else if (anyUnapproved) {
                Button(
                    onClick = {
                        viewModel.rejectTimesheet(submitterId, weekStart)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(timesheetRejectedMsg)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(rejectTimesheetLabel)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn {
                items(entries) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(id = R.string.project_label_inline, entry.projectId))
                            Text(stringResource(id = R.string.subcategory_label_inline, entry.subcategoryId))
                            Text(stringResource(id = R.string.hours_label_inline, entry.dailyHours.joinToString()))
                            Text(stringResource(id = R.string.approved_label, if (entry.approved) "✅" else "❌"))
                            if (!entry.approved) {
                                Button(
                                    onClick = {
                                        viewModel.approveEntry(submitterId, weekStart, entry.id)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(entryApprovedMsg)
                                        }
                                    },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text(approveEntryLabel)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}