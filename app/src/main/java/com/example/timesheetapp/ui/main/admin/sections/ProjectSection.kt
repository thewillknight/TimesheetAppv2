package com.example.timesheetapp.ui.main.admin.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.viewmodel.ProjectViewModel

@Composable
fun ProjectSection(viewModel: ProjectViewModel = viewModel()) {
    val projects by viewModel.projects.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var newProjectName by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }

    // Show snackbar when triggered
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // .padding(paddingValues = padding)
                .padding(horizontal = 16.dp)
        ) {
            Text("Add New Project", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newProjectName,
                onValueChange = { newProjectName = it },
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.addProject(newProjectName) {
                        newProjectName = ""
                        snackbarMessage = "Project added."
                    }
                },
                enabled = newProjectName.isNotBlank() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Add Project")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Existing Projects", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(projects) { project ->
                    Text("• ${project.id}: ${project.name}")
                }
            }
        }
    }
}
