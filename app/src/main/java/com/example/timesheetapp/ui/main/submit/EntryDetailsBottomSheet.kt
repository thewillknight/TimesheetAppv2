package com.example.timesheetapp.ui.main.submit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.data.model.Project
import com.example.timesheetapp.data.model.Subcategory
import com.example.timesheetapp.data.model.TimesheetEntry
import com.example.timesheetapp.viewmodel.ProjectViewModel
import com.example.timesheetapp.viewmodel.SubcategoryViewModel
import com.example.timesheetapp.viewmodel.TimesheetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailBottomSheet(
    weekStart: String,
    existingEntry: TimesheetEntry? = null,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 500.dp) // force taller sheet
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val timesheetViewModel: TimesheetViewModel = viewModel()
        val projectViewModel: ProjectViewModel = viewModel()
        val subcategoryViewModel: SubcategoryViewModel = viewModel()

        val projects by projectViewModel.projects.collectAsState()
        val subcategories by subcategoryViewModel.subcategories.collectAsState()

        var selectedProjectId by remember { mutableStateOf(existingEntry?.projectId ?: "") }
        var selectedSubcategoryId by remember { mutableStateOf(existingEntry?.subcategoryId ?: "") }
        var dailyHours by remember { mutableStateOf(existingEntry?.dailyHours ?: List(7) { 0.0 }) }

        val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        LaunchedEffect(Unit) {
            projectViewModel.loadProjects()
            subcategoryViewModel.loadSubcategories()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Timesheet Entry", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Project Dropdown
            var expandedProject by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedProject,
                onExpandedChange = { expandedProject = !expandedProject }
            ) {
                OutlinedTextField(
                    value = selectedProjectId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Project") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedProject,
                    onDismissRequest = { expandedProject = false }
                ) {
                    projects.forEach { project ->
                        DropdownMenuItem(
                            text = { Text("${project.id} - ${project.name}") },
                            onClick = {
                                selectedProjectId = project.id
                                expandedProject = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subcategory Dropdown
            var expandedSubcategory by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedSubcategory,
                onExpandedChange = { expandedSubcategory = !expandedSubcategory }
            ) {
                OutlinedTextField(
                    value = selectedSubcategoryId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Subcategory") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedSubcategory,
                    onDismissRequest = { expandedSubcategory = false }
                ) {
                    subcategories.forEach { sub ->
                        DropdownMenuItem(
                            text = { Text("${sub.code} - ${sub.description}") },
                            onClick = {
                                selectedSubcategoryId = sub.code
                                expandedSubcategory = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Hours Per Day", style = MaterialTheme.typography.titleMedium)

            dayLabels.forEachIndexed { index, day ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("$day: ${"%.1f".format(dailyHours[index])} hrs")
                    }

                    Slider(
                        value = dailyHours[index].toFloat(),
                        onValueChange = { newValue ->
                            dailyHours =
                                dailyHours.toMutableList().also { it[index] = newValue.toDouble() }
                        },
                        valueRange = 0f..12f,
                        steps = 23
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newEntry = TimesheetEntry(
                        id = existingEntry?.id ?: "",
                        projectId = selectedProjectId,
                        subcategoryId = selectedSubcategoryId,
                        dailyHours = dailyHours
                    )
                    timesheetViewModel.addOrUpdateEntry(weekStart, newEntry)
                    onDismiss()
                },
                enabled = selectedProjectId.isNotBlank() && selectedSubcategoryId.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Entry")
            }
        }
    }
}
