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
import java.util.*

@Composable
fun EntryDetailBottomSheet(
    weekStart: String,
    existingEntry: TimesheetEntry? = null,
    isEditable: Boolean = true,
    onDismiss: () -> Unit,
    viewModel: TimesheetViewModel = viewModel()
) {
    var projectId by remember { mutableStateOf(existingEntry?.projectId ?: "") }
    var subcategoryId by remember { mutableStateOf(existingEntry?.subcategoryId ?: "") }
    var dailyHours by remember { mutableStateOf(existingEntry?.dailyHours ?: List(7) { 0.0 }) }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

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

        OutlinedTextField(
            value = projectId,
            onValueChange = { projectId = it },
            label = { Text("Project ID") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditable
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = subcategoryId,
            onValueChange = { subcategoryId = it },
            label = { Text("Subcategory ID") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditable
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Hours per day", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

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
                steps = 23 // 0.5 hour increments (24 values = 23 steps)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditable) {
            Button(
                onClick = {
                    val entry = TimesheetEntry(
                        id = existingEntry?.id ?: "", // new or existing
                        projectId = projectId,
                        subcategoryId = subcategoryId,
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
