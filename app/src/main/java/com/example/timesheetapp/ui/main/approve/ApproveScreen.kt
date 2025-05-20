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
import androidx.navigation.NavController
import com.example.timesheetapp.viewmodel.ApproveViewModel
import com.example.timesheetapp.viewmodel.SubmitterApprovalStatus

@Composable
fun ApproveScreen(
    navHostController: NavController,
    viewModel: ApproveViewModel = viewModel()
) {
    val submitters by viewModel.submitters.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSubmitters()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Timesheets to Approve", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (submitters.isEmpty()) {
            Text("You have no delegated submitters.")
        } else {
            LazyColumn {
                items(submitters) { submitterStatus ->
                    SubmitterApprovalCard(submitterStatus) {
                        navHostController.navigate("submitterTimesheets/${submitterStatus.user.id}/${submitterStatus.user.firstName}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SubmitterApprovalCard(
    submitter: SubmitterApprovalStatus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${submitter.user.firstName} ${submitter.user.lastName}")
            Text(
                text = if (submitter.hasUnapprovedTimesheets)
                    "⚠️ Outstanding timesheets need approval"
                else
                    "✅ All timesheets approved"
            )
        }
    }
}
