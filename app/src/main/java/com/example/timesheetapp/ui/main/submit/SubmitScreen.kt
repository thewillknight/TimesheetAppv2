package com.example.timesheetapp.ui.main.submit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.timesheetapp.R
import com.example.timesheetapp.data.model.Timesheet
import com.example.timesheetapp.viewmodel.TimesheetViewModel
import java.util.*

@Composable
fun SubmitScreen(
    navController: NavController,
    viewModel: TimesheetViewModel = viewModel()
) {
    val timesheets by viewModel.timesheets.collectAsState()
    var newlyCreatedWeek by remember { mutableStateOf<String?>(null) }

    val addTimesheetContentDescription = stringResource(id = R.string.add_timesheet_content_description)
    val yourTimesheetsTitle = stringResource(id = R.string.your_timesheets_title)

    LaunchedEffect(Unit) {
        viewModel.loadTimesheets()
    }

    LaunchedEffect(timesheets, newlyCreatedWeek) {
        newlyCreatedWeek?.let { week ->
            if (timesheets.any { it.weekStart == week }) {
                navController.navigate("timesheetSummary/$week") {
                    launchSingleTop = true
                }
                newlyCreatedWeek = null
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addTimesheetForNextWeek()
                newlyCreatedWeek = viewModel.computeNextWeekStart()
            }) {
                Icon(Icons.Default.Add, contentDescription = addTimesheetContentDescription)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(yourTimesheetsTitle, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(timesheets.sortedByDescending { it.weekStart }) { timesheet ->
                    TimesheetListItem(timesheet = timesheet) {
                        navController.navigate("timesheetSummary/${timesheet.weekStart}") {
                            launchSingleTop = true
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TimesheetListItem(timesheet: Timesheet, onClick: () -> Unit) {
    val statusColor = when (timesheet.status.lowercase(Locale.getDefault())) {
        "draft", "rejected" -> Color(0xFFFFCDD2)
        "pending" -> Color(0xFFFFF9C4)
        "approved" -> Color(0xFFC8E6C9)
        else -> Color.LightGray
    }

    val statusRejectedText = stringResource(id = R.string.status_rejected)
    val statusLabelTemplate = stringResource(id = R.string.status_label)
    val weekLabelTemplate = stringResource(id = R.string.week_label)

    val statusText = when (timesheet.status.lowercase()) {
        "rejected" -> statusRejectedText
        else -> timesheet.status.replaceFirstChar { it.uppercaseChar() }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .background(statusColor)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(String.format(weekLabelTemplate, timesheet.weekStart), style = MaterialTheme.typography.bodyLarge)
                Text(String.format(statusLabelTemplate, statusText))
            }
        }
    }
}
