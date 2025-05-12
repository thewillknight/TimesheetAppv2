package com.example.timesheetapp.ui.main.admin.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.viewmodel.SubcategoryViewModel

@Composable
fun SubcategorySection(viewModel: SubcategoryViewModel = viewModel()) {
    val subcategories by viewModel.subcategories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var newCode by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadSubcategories()
    }

    // Trigger snackbar when message is set
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold( modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
               // .padding(paddingValues = padding)
                .padding(horizontal = 16.dp)
        ) {
            Text("Add New Subcategory", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newCode,
                onValueChange = { newCode = it },
                label = { Text("Code (e.g. TRAV)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newDescription,
                onValueChange = { newDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val code = newCode.trim().uppercase()
                    val desc = newDescription.trim()

                    if (subcategories.any { it.code.equals(code, ignoreCase = true) }) {
                        snackbarMessage = "Subcategory code \"$code\" already exists."
                    } else {
                        viewModel.addSubcategory(code, desc) {
                            newCode = ""
                            newDescription = ""
                            snackbarMessage = "Subcategory \"$code\" added."
                        }
                    }
                },
                enabled = newCode.isNotBlank() && newDescription.isNotBlank() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Add Subcategory")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Existing Subcategories", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(subcategories) { sub ->
                    Text("• ${sub.code}: ${sub.description}")
                }
            }
        }
    }
}
