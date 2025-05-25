package com.example.timesheetapp.ui.main.admin.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.R
import com.example.timesheetapp.ui.components.SearchableDropdownMenuBox
import com.example.timesheetapp.viewmodel.AdminViewModel

@Composable
fun DelegationSection(viewModel: AdminViewModel = viewModel()) {
    val users by viewModel.users.collectAsState()
    val currentApprovers by viewModel.currentApprovers.collectAsState()

    var selectedSubmitterId by remember { mutableStateOf<String?>(null) }
    val selectedSubmitter = users.find { it.id == selectedSubmitterId }

    var searchApprover by remember { mutableStateOf("") }

    val assignApproversTitle = stringResource(id = R.string.assign_approvers_title)
    val selectSubmitterLabel = stringResource(id = R.string.select_submitter_label)
    val searchApproversLabel = stringResource(id = R.string.search_approvers_label)

    LaunchedEffect(Unit) {
        viewModel.loadAllUsers()
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(assignApproversTitle, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(12.dp))

        // Submitter Selection
        SearchableDropdownMenuBox(
            label = selectSubmitterLabel,
            options = users,
            selectedId = selectedSubmitterId,
            getId = { it.id },
            getLabel = { viewModel.getFullName(it.user) },
            onSelect = {
                selectedSubmitterId = it
                viewModel.loadApproversForSubmitter(it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedSubmitter != null) {
            val selectApproversText = stringResource(
                id = R.string.select_approvers_label,
                viewModel.getFullName(selectedSubmitter.user)
            )

            Text(selectApproversText)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchApprover,
                onValueChange = { searchApprover = it },
                label = { Text(searchApproversLabel) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            val filteredApprovers = users
                .filter { it.id != selectedSubmitterId }
                .filter {
                    viewModel.getFullName(it.user)
                        .contains(searchApprover, ignoreCase = true)
                }

            Column {
                filteredApprovers.forEach { potentialApprover ->
                    val isChecked = currentApprovers.contains(potentialApprover.id)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    viewModel.delegateApprover(
                                        approverId = potentialApprover.id,
                                        submitterId = selectedSubmitterId!!
                                    )
                                } else {
                                    viewModel.revokeDelegation(
                                        approverId = potentialApprover.id,
                                        submitterId = selectedSubmitterId!!
                                    )
                                }
                                viewModel.loadApproversForSubmitter(selectedSubmitterId!!)
                            }
                        )
                        Text(
                            viewModel.getFullName(potentialApprover.user),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
