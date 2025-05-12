package com.example.timesheetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timesheetapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
    object Idle : AuthResult()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _authState.value = if (task.isSuccessful) {
                        AuthResult.Success
                    } else {
                        AuthResult.Error(task.exception?.message ?: "Login failed.")
                    }
                }
        }
    }

    fun signUp(firstName: String, lastName: String, email: String, password: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val db = FirebaseFirestore.getInstance()
                        val user = User(
                            email = email,
                            firstName = firstName,
                            lastName = lastName)
                        db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                _authState.value = AuthResult.Success
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthResult.Error("Firestore write failed: ${e.localizedMessage}")
                            }
                    } else {
                        _authState.value = AuthResult.Error(task.exception?.message ?: "Signup failed.")
                    }
                }
        }
    }


    fun resetState() {
        _authState.value = AuthResult.Idle
    }
}
