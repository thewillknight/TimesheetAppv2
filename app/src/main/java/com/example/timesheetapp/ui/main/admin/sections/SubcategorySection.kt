package com.example.timesheetapp.ui.main.admin.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timesheetapp.R
import com.example.timesheetapp.viewmodel.SubcategoryViewModel

@Composable
fun SubcategorySection(viewModel: SubcategoryViewModel = viewModel()) {
    val subcategories by viewModel.subcategories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var newCode by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val title = stringResource(id = R.string.add_new_subcategory_title)
    val codeLabel = stringResource(id = R.string.code_label)
    val descriptionLabel = stringResource(id = R.string.description_label)
    val addButton = stringResource(id = R.string.add_subcategory_button)
    val existingTitle = stringResource(id = R.string.existing_subcategories_title)
    val context = LocalContext.current


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

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newCode,
                onValueChange = { newCode = it },
                label = { Text(codeLabel) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newDescription,
                onValueChange = { newDescription = it },
                label = { Text(descriptionLabel) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val code = newCode.trim().uppercase()
                    val desc = newDescription.trim()

                    if (subcategories.any { it.code.equals(code, ignoreCase = true) }) {
                        snackbarMessage = context.getString(R.string.subcategory_exists_message, code)
                    } else {
                        viewModel.addSubcategory(code, desc) {
                            newCode = ""
                            newDescription = ""
                            snackbarMessage = context.getString(R.string.subcategory_added_message, code)
                        }
                    }

                },
                enabled = newCode.isNotBlank() && newDescription.isNotBlank() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(addButton)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(existingTitle, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(subcategories) { sub ->
                    Text(stringResource(id = R.string.subcategory_list_item, sub.code, sub.description))
                }
            }
        }
    }
}
