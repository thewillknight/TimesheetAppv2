package com.example.timesheetapp.data.model

data class Timesheet(
    val userId: String = "",
    val weekStart: String = "",  // keep as ISO string in ViewModel/UI
    val status: String = "draft",
    val submittedAt: String? = null,
    val approvedAt: String? = null
)
