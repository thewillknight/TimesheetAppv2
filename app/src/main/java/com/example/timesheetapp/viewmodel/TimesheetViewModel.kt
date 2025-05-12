package com.example.timesheetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timesheetapp.data.model.Timesheet
import com.example.timesheetapp.data.model.TimesheetEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class TimesheetViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _timesheets = MutableStateFlow<List<Timesheet>>(emptyList())
    val timesheets: StateFlow<List<Timesheet>> = _timesheets

    private val _entries = MutableStateFlow<List<TimesheetEntry>>(emptyList())
    val entries: StateFlow<List<TimesheetEntry>> = _entries

    private val userId: String
        get() = auth.currentUser?.uid.orEmpty()

    private fun getStartOfWeek(date: LocalDate): LocalDate {
        return date.with(DayOfWeek.MONDAY)
    }

    fun loadTimesheets() {
        if (userId.isBlank()) return

        viewModelScope.launch {
            val snapshot = db.collection("timesheets")
                .document(userId)
                .collection("records")
                .get()
                .await()

            _timesheets.value = snapshot.documents.mapNotNull { it.toObject(Timesheet::class.java) }
        }
    }

    fun addTimesheetForNextWeek() {
        if (userId.isBlank()) return

        viewModelScope.launch {
            // Re-fetch from Firestore to get fresh records (or rely on already-loaded _timesheets)
            val snapshot = db.collection("timesheets")
                .document(userId)
                .collection("records")
                .get()
                .await()

            val timesheets = snapshot.documents.mapNotNull { it.toObject(Timesheet::class.java) }

            // Determine weekStart
            val weekStartDate = if (timesheets.isEmpty()) {
                // No existing records: use most recent past Monday
                getMostRecentMonday()
            } else {
                // Existing records: find latest week and add 1 week
                val latestWeek = timesheets.maxOfOrNull { LocalDate.parse(it.weekStart) } ?: getMostRecentMonday()
                latestWeek.plusWeeks(1)
            }

            val weekStartStr = weekStartDate.toString()

            val newTimesheet = Timesheet(
                userId = userId,
                weekStart = weekStartStr,
                status = "draft"
            )

            db.collection("timesheets")
                .document(userId)
                .collection("records")
                .document(weekStartStr)
                .set(newTimesheet)
                .await()

            loadTimesheets()
        }
    }


    fun loadEntries(weekStart: String) {
        if (userId.isBlank()) return

        viewModelScope.launch {
            val snapshot = db.collection("timesheets")
                .document(userId)
                .collection("records")
                .document(weekStart)
                .collection("entries")
                .get()
                .await()

            _entries.value = snapshot.documents.mapNotNull { it.toObject(TimesheetEntry::class.java) }
        }
    }

    fun addOrUpdateEntry(weekStart: String, entry: TimesheetEntry) {
        if (userId.isBlank()) return

        val entryId = entry.id.ifBlank { UUID.randomUUID().toString() }
        val entryToSave = entry.copy(id = entryId)

        viewModelScope.launch {
            db.collection("timesheets")
                .document(userId)
                .collection("records")
                .document(weekStart)
                .collection("entries")
                .document(entryId)
                .set(entryToSave)
                .await()

            loadEntries(weekStart)
        }
    }

    private fun getMostRecentMonday(): LocalDate {
        val today = LocalDate.now()
        return today.with(DayOfWeek.MONDAY).let {
            if (today.dayOfWeek < DayOfWeek.MONDAY) it.minusWeeks(1) else it
        }
    }

    fun computeNextWeekStart(): String {
        val latest = _timesheets.value.mapNotNull {
            runCatching { LocalDate.parse(it.weekStart) }.getOrNull()
        }.maxOrNull() ?: LocalDate.now().with(java.time.DayOfWeek.MONDAY)

        return latest.plusWeeks(1).toString()
    }

    fun submitTimesheet(weekStart: String) {
        val userId = auth.currentUser?.uid ?: return
        val update = mapOf(
            "status" to "pending",
            "submittedAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("timesheets")
            .document(userId)
            .collection("records")
            .document(weekStart)
            .update(update)
            .addOnSuccessListener {
                loadTimesheets()
            }
    }



}
