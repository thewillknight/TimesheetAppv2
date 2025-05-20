package com.example.timesheetapp.ui.main.approve

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.timesheetapp.data.model.Timesheet
import com.example.timesheetapp.viewmodel.ApproveViewModel

@Composable
fun SubmitterTimesheetsScreen(
    navController: NavController,
    submitterId: String,
    submitterName: String,
    viewModel: ApproveViewModel = viewModel()
) {
    val timesheets by viewModel.selectedSubmitterTimesheets.collectAsState()

    LaunchedEffect(submitterId) {
        viewModel.loadSubmitterTimesheets(submitterId)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "$submitterName's Timesheets",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(timesheets) { timesheet ->
                    TimesheetListItem(timesheet) {
                        navController.navigate("approveSummary/${submitterId}/${timesheet.weekStart}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TimesheetListItem(timesheet: Timesheet, onClick: () -> Unit) {
    val statusColor = when (timesheet.status.lowercase()) {
        "draft" -> Color(0xFFFFCDD2)
        "pending" -> Color(0xFFFFF9C4)
        "approved" -> Color(0xFFC8E6C9)
        else -> Color.LightGray
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
                Text("Week: ${timesheet.weekStart}", style = MaterialTheme.typography.bodyLarge)
                Text("Status: ${timesheet.status.replaceFirstChar { it.uppercaseChar() }}")
            }
        }
    }
}
