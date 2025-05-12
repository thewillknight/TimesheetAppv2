package com.example.timesheetapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

@Composable
fun <T> SearchableDropdownMenuBox(
    label: String,
    options: List<T>,
    selectedId: String?,
    getId: (T) -> String = { it.toString() },
    getLabel: (T) -> String,
    onSelect: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    val selectedLabel = options.find { getId(it) == selectedId }?.let(getLabel) ?: ""
    val showQuery = isFocused || selectedId == null

    val filteredOptions = if (query.isNotBlank()) {
        options.filter { getLabel(it).contains(query, ignoreCase = true) }
    } else {
        emptyList()
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            OutlinedTextField(
                value = if (showQuery) query else selectedLabel,
                onValueChange = {
                    query = it
                },
                label = { Text(label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                trailingIcon = {
                    IconButton(onClick = {
                        isFocused = !isFocused
                        if (!isFocused) query = ""
                    }) {
                        Icon(
                            imageVector = if (isFocused) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Toggle dropdown"
                        )
                    }
                },
                singleLine = true
            )

            if (isFocused && filteredOptions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                ) {
                    items(filteredOptions) { item ->
                        Text(
                            text = getLabel(item),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(getId(item))
                                    query = ""
                                    isFocused = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }

            if (isFocused && query.isNotBlank() && filteredOptions.isEmpty()) {
                Text(
                    text = "No results",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


