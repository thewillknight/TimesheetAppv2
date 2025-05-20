package com.example.timesheetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timesheetapp.data.model.Timesheet
import com.example.timesheetapp.data.model.TimesheetEntry
import com.example.timesheetapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SubmitterApprovalStatus(
    val user: User,
    val hasUnapprovedTimesheets: Boolean
)

class ApproveViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _submitters = MutableStateFlow<List<SubmitterApprovalStatus>>(emptyList())
    val submitters: StateFlow<List<SubmitterApprovalStatus>> = _submitters

    fun loadSubmitters() {
        val approverId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            db.collection("delegations")
                .document(approverId)
                .collection("users")
                .get()
                .addOnSuccessListener { delegationSnapshot ->
                    val submitterIds = delegationSnapshot.documents.map { it.id }
                    println("DEBUG: Found delegated submitters = $submitterIds")

                    if (submitterIds.isEmpty()) {
                        _submitters.value = emptyList()
                        return@addOnSuccessListener
                    }

                    db.collection("users")
                        .whereIn(FieldPath.documentId(), submitterIds)
                        .get()
                        .addOnSuccessListener { userSnapshot ->
                            val users = userSnapshot.documents.mapNotNull { doc ->
                                println("DEBUG: Retrieved doc ID = ${doc.id}, data = ${doc.data}")
                                doc.toObject(User::class.java)?.copy(id = doc.id)
                            }
                            println("DEBUG: Loaded user documents = ${users.map { it.firstName }}")

                            val statusList = mutableListOf<SubmitterApprovalStatus>()
                            if (users.isEmpty()) {
                                _submitters.value = emptyList()
                                return@addOnSuccessListener
                            }

                            users.forEach { user ->
                                checkUnapprovedTimesheets(user) { hasUnapproved ->
                                    statusList.add(SubmitterApprovalStatus(user, hasUnapproved))
                                    if (statusList.size == users.size) {
                                        _submitters.value = statusList.sortedBy { it.user.firstName }
                                    }
                                }
                            }
                        }
                }
        }
    }

    private fun checkUnapprovedTimesheets(user: User, callback: (Boolean) -> Unit) {
        db.collection("timesheets")
            .document(user.id)
            .collection("records")
            .whereEqualTo("status", "pending") // Any pending record = needs attention
            .get()
            .addOnSuccessListener { timesheetSnapshot ->
                callback(!timesheetSnapshot.isEmpty) // If any pending records exist, return true
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    val selectedSubmitterTimesheets = MutableStateFlow<List<Timesheet>>(emptyList())

    fun loadSubmitterTimesheets(submitterId: String) {
        db.collection("timesheets")
            .document(submitterId)
            .collection("records")
            .whereIn("status", listOf("pending", "approved"))
            .get()
            .addOnSuccessListener { snapshot ->
                val timesheets = snapshot.documents.mapNotNull { doc ->
                    val ts = doc.toObject(com.example.timesheetapp.data.model.Timesheet::class.java)
                    ts?.copy(weekStart = doc.id, userId = submitterId)
                }.sortedByDescending { it.weekStart }
                selectedSubmitterTimesheets.value = timesheets
            }
    }

    val selectedTimesheetEntries = MutableStateFlow<List<TimesheetEntry>>(emptyList())

    fun loadTimesheetEntries(submitterId: String, weekStart: String) {
        db.collection("timesheets")
            .document(submitterId)
            .collection("records")
            .document(weekStart)
            .collection("entries")
            .get()
            .addOnSuccessListener { snapshot ->
                val entries = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TimesheetEntry::class.java)
                }
                selectedTimesheetEntries.value = entries
            }
    }

    fun approveEntry(submitterId: String, weekStart: String, entryId: String) {
        val approverId = auth.currentUser?.uid ?: return
        db.collection("timesheets")
            .document(submitterId)
            .collection("records")
            .document(weekStart)
            .collection("entries")
            .document(entryId)
            .update(
                mapOf(
                    "approved" to true,
                    "approvedBy" to approverId
                )
            )
            .addOnSuccessListener {
                loadTimesheetEntries(submitterId, weekStart)
            }
    }

    fun approveTimesheet(submitterId: String, weekStart: String) {
        db.collection("timesheets")
            .document(submitterId)
            .collection("records")
            .document(weekStart)
            .update("status", "approved")
    }

    fun rejectTimesheet(submitterId: String, weekStart: String) {
        val recordRef = db.collection("timesheets")
            .document(submitterId)
            .collection("records")
            .document(weekStart)

        recordRef.update("status", "rejected").addOnSuccessListener {
            recordRef.collection("entries")
                .get()
                .addOnSuccessListener { snapshot ->
                    val batch = db.batch()
                    snapshot.forEach { doc ->
                        batch.update(doc.reference, mapOf("approved" to false, "approvedBy" to null))
                    }
                    batch.commit().addOnSuccessListener {
                        loadTimesheetEntries(submitterId, weekStart)
                    }
                }
        }
    }


}
