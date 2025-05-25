package com.example.timesheetapp.ui.main.submit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.R
import com.example.timesheetapp.data.model.TimesheetEntry
import com.example.timesheetapp.viewmodel.TimesheetViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.TextStyle
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

    val editEntryTitle = stringResource(id = R.string.edit_entry_title)
    val newEntryTitle = stringResource(id = R.string.new_entry_title)
    val projectLabel = stringResource(id = R.string.project_label)
    val subcategoryLabel = stringResource(id = R.string.subcategory_label)
    val hoursPerDayLabel = stringResource(id = R.string.hours_per_day_label)
    val saveEntryButton = stringResource(id = R.string.save_entry_button)
    val hoursDisplayFormat = stringResource(id = R.string.hours_display_format)

    val daysOfWeek = DayOfWeek.entries.map {
        it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

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
            text = if (existingEntry != null) editEntryTitle else newEntryTitle,
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
                label = { Text(projectLabel) },
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
                label = { Text(subcategoryLabel) },
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
        Text(hoursPerDayLabel, style = MaterialTheme.typography.titleSmall)

        daysOfWeek.forEachIndexed { index, day ->
            Text(String.format(hoursDisplayFormat, day, dailyHours[index]))
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
                Text(saveEntryButton)
            }
        }
    }
}

