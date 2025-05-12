package com.example.timesheetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timesheetapp.data.model.Subcategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SubcategoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _subcategories = MutableStateFlow<List<Subcategory>>(emptyList())
    val subcategories: StateFlow<List<Subcategory>> = _subcategories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadSubcategories() {
        viewModelScope.launch {
            val snapshot = db.collection("subcategories").get().await()
            val items = snapshot.documents.mapNotNull { doc ->
                val code = doc.getString("code") ?: return@mapNotNull null
                val description = doc.getString("description") ?: return@mapNotNull null
                Subcategory(code = code, description = description)
            }.sortedBy { it.code }
            _subcategories.value = items
        }
    }

    fun addSubcategory(code: String, description: String, onComplete: () -> Unit) {
        val trimmedCode = code.trim().uppercase()
        val trimmedDesc = description.trim()

        // Check for duplicate
        if (_subcategories.value.any { it.code == trimmedCode }) {
            onComplete() // already exists — skip add
            return
        }

        val subcategory = Subcategory(code = trimmedCode, description = trimmedDesc)
        _isLoading.value = true

        db.collection("subcategories")
            .document()
            .set(subcategory)
            .addOnSuccessListener {
                _subcategories.value = _subcategories.value + subcategory
                onComplete()
            }
            .addOnCompleteListener {
                _isLoading.value = false
            }
    }
}
