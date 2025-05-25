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
import androidx.navigation.NavController
import com.example.timesheetapp.R
import com.example.timesheetapp.viewmodel.ApproveViewModel
import com.example.timesheetapp.viewmodel.SubmitterApprovalStatus

@Composable
fun ApproveScreen(
    navHostController: NavController,
    viewModel: ApproveViewModel = viewModel()
) {
    val submitters by viewModel.submitters.collectAsState()

    val screenTitle = stringResource(id = R.string.approve_screen_title)
    val noSubmittersMessage = stringResource(id = R.string.no_submitters_message)

    LaunchedEffect(Unit) {
        viewModel.loadSubmitters()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(screenTitle, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (submitters.isEmpty()) {
            Text(noSubmittersMessage)
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
    val statusPending = stringResource(id = R.string.timesheets_pending)
    val statusApproved = stringResource(id = R.string.timesheets_approved)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${submitter.user.firstName} ${submitter.user.lastName}")
            Text(
                text = if (submitter.hasUnapprovedTimesheets)
                    statusPending
                else
                    statusApproved
            )
        }
    }
}
