package com.example.timesheetapp.ui.main.submit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.data.model.TimesheetEntry
import com.example.timesheetapp.viewmodel.TimesheetViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailBottomSheet(
    weekStart: String,
    existingEntry: TimesheetEntry? = null,
    isEditable: Boolean = true,
    onDismiss: () -> Unit,
    viewModel: TimesheetViewModel = viewModel()
) {
    var selectedProjectId by remember { mutableStateOf(existingEntry?.projectId ?: "") }
    var selectedSubcategoryCode by remember { mutableStateOf(existingEntry?.subcategoryId ?: "") }
    var dailyHours by remember { mutableStateOf(existingEntry?.dailyHours ?: List(7) { 0.0 }) }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val projects by viewModel.projects.collectAsState()
    val subcategories by viewModel.subcategories.collectAsState()

    var projectDropdownExpanded by remember { mutableStateOf(false) }
    var subcategoryDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
        viewModel.loadSubcategories()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 500.dp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (existingEntry != null) "Edit Entry" else "New Entry",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Project Dropdown
        ExposedDropdownMenuBox(
            expanded = projectDropdownExpanded,
            onExpandedChange = { projectDropdownExpanded = !projectDropdownExpanded }
        ) {
            OutlinedTextField(
                value = projects.find { it.id == selectedProjectId }?.let { "${it.id}: ${it.name}" } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Project") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                enabled = isEditable
            )
            ExposedDropdownMenu(
                expanded = projectDropdownExpanded,
                onDismissRequest = { projectDropdownExpanded = false }
            ) {
                projects.forEach { project ->
                    DropdownMenuItem(
                        text = { Text("${project.id}: ${project.name}") },
                        onClick = {
                            selectedProjectId = project.id
                            projectDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subcategory Dropdown
        ExposedDropdownMenuBox(
            expanded = subcategoryDropdownExpanded,
            onExpandedChange = { subcategoryDropdownExpanded = !subcategoryDropdownExpanded }
        ) {
            OutlinedTextField(
                value = subcategories.find { it.code == selectedSubcategoryCode }?.let { "${it.code}: ${it.description}" } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Subcategory") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                enabled = isEditable
            )
            ExposedDropdownMenu(
                expanded = subcategoryDropdownExpanded,
                onDismissRequest = { subcategoryDropdownExpanded = false }
            ) {
                subcategories.forEach { sub ->
                    DropdownMenuItem(
                        text = { Text("${sub.code}: ${sub.description}") },
                        onClick = {
                            selectedSubcategoryCode = sub.code
                            subcategoryDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Hours per day", style = MaterialTheme.typography.titleSmall)

        daysOfWeek.forEachIndexed { index, day ->
            Text("$day: ${dailyHours[index]} hrs")
            Slider(
                value = dailyHours[index].toFloat(),
                onValueChange = { newValue ->
                    val clamped = newValue.coerceIn(0f, 12f)
                    dailyHours = dailyHours.toMutableList().also { it[index] = clamped.toDouble() }
                },
                enabled = isEditable,
                valueRange = 0f..12f,
                steps = 23
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditable) {
            Button(
                onClick = {
                    val entry = TimesheetEntry(
                        id = existingEntry?.id ?: "",
                        projectId = selectedProjectId,
                        subcategoryId = selectedSubcategoryCode,
                        dailyHours = dailyHours,
                        approved = existingEntry?.approved ?: false,
                        approvedBy = existingEntry?.approvedBy
                    )
                    viewModel.addOrUpdateEntry(weekStart, entry)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Entry")
            }
        }
    }
}
