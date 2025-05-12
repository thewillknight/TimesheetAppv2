package com.example.timesheetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timesheetapp.data.model.Project
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProjectViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadProjects() {
        viewModelScope.launch {
            val snapshot = db.collection("projects").get().await()
            val items = snapshot.documents.mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                val id = doc.getString("id") ?: doc.id
                Project(id = id, name = name)
            }.sortedBy { it.id.removePrefix("P").toIntOrNull() ?: Int.MAX_VALUE }
            _projects.value = items
        }
    }

    fun addProject(name: String, onComplete: () -> Unit) {
        val nextId = generateNextProjectId(_projects.value)
        val project = Project(id = nextId, name = name.trim())

        _isLoading.value = true

        db.collection("projects")
            .document()
            .set(project)
            .addOnSuccessListener {
                _projects.value = _projects.value + project
                onComplete()
            }
            .addOnFailureListener {
                // Log or toast error if needed
            }
            .addOnCompleteListener {
                _isLoading.value = false
            }
    }

    private fun generateNextProjectId(projects: List<Project>): String {
        val maxId = projects.mapNotNull {
            it.id.removePrefix("P").toIntOrNull()
        }.maxOrNull() ?: 0
        return "P${maxId + 1}"
    }
}
