package com.example.timesheetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timesheetapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun loadUserData() {
        val uid = FirebaseAuth.getInstance().uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                _user.value = doc.toObject(User::class.java)
            }
    }

    private val _isApprover = MutableStateFlow(false)
    val isApprover: StateFlow<Boolean> = _isApprover

    fun checkIfUserIsApprover() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("delegations")
            .document(uid)
            .collection("users")
            .limit(1) // just need to know if one exists
            .get()
            .addOnSuccessListener { snapshot ->
                _isApprover.value = !snapshot.isEmpty
            }
    }

}
