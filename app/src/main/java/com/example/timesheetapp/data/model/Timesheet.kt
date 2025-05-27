package com.example.timesheetapp.data.model
import com.google.firebase.Timestamp


data class Timesheet(
    val userId: String = "",
    val weekStart: String = "",
    val status: String = "draft",
    val submittedAt: Timestamp? = null,
    val approvedAt: Timestamp? = null
)
