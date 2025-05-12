package com.example.timesheetapp.ui.main.admin.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.ui.components.SearchableDropdownMenuBox
import com.example.timesheetapp.viewmodel.AdminViewModel

@Composable
fun DelegationSection(viewModel: AdminViewModel = viewModel()) {
    val users by viewModel.users.collectAsState()
    val currentApprovers by viewModel.currentApprovers.collectAsState()

    var selectedSubmitterId by remember { mutableStateOf<String?>(null) }
    val selectedSubmitter = users.find { it.id == selectedSubmitterId }

    var searchApprover by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadAllUsers()
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text("Assign Approvers", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(12.dp))

        // Submitter Selection
        SearchableDropdownMenuBox(
            label = "Select Submitter",
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
            Text("Select approvers for ${viewModel.getFullName(selectedSubmitter.user)}")

            Spacer(modifier = Modifier.height(8.dp))

            // Search field for filtering potential approvers
            OutlinedTextField(
                value = searchApprover,
                onValueChange = { searchApprover = it },
                label = { Text("Search Approvers") },
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
                    println("Checkbox for ${potentialApprover.id} = $isChecked")

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
                                // Reload current approvers
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
