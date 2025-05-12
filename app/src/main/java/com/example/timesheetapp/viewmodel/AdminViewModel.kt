package com.example.timesheetapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timesheetapp.data.model.Delegation
import com.example.timesheetapp.data.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant



class AdminViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // User wrapper with Firestore document ID
    data class UserWithId(
        val id: String,
        val user: User
    )

    // Holds all users in the system
    private val _users = MutableStateFlow<List<UserWithId>>(emptyList())
    val users: StateFlow<List<UserWithId>> = _users

    // Load all users (for dropdowns and delegation)
    fun loadAllUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                _users.value = snapshot.documents.mapNotNull { doc ->
                    val user = doc.toObject(User::class.java)
                    if (user != null) UserWithId(doc.id, user) else null
                }
            }
        Log.d("AdminVM", "Loaded users: ${_users.value}")
    }

    // Helper to get full name from User
    fun getFullName(user: User): String {
        return "${user.firstName} ${user.lastName}".trim()
    }

    private val _currentApprovers = MutableStateFlow<Set<String>>(emptySet())
    val currentApprovers: StateFlow<Set<String>> = _currentApprovers

    fun loadApproversForSubmitter(submitterId: String) {
        db.collectionGroup("users")
            .whereEqualTo("submitterId", submitterId)
            .get()
            .addOnSuccessListener { snapshot ->
                val approverIds = snapshot.documents.mapNotNull {
                    val approverId = it.reference.parent?.parent?.id
                    println("Found delegation: approverId = $approverId for submitterId = $submitterId")
                    approverId
                }.toSet()

                println("Final approver list for $submitterId: $approverIds")

                _currentApprovers.value = approverIds
            }
    }


    // Assign an approver to a submitter
    fun delegateApprover(approverId: String, submitterId: String) {
        val currentUserId = auth.uid ?: return

        val delegation = Delegation(
            canApprove = true,
            addedAt = Instant.now().toString(),
            addedBy = currentUserId,
            submitterId = submitterId
        )


        db.collection("delegations")
            .document(approverId)
            .collection("users")
            .document(submitterId)
            .set(delegation)
            .addOnSuccessListener {
                loadApproversForSubmitter(submitterId)
            }
    }


    fun revokeDelegation(approverId: String, submitterId: String) {
        db.collection("delegations")
            .document(approverId)
            .collection("users")
            .document(submitterId)
            .delete()
            .addOnSuccessListener {
                loadApproversForSubmitter(submitterId)
            }
    }
}
