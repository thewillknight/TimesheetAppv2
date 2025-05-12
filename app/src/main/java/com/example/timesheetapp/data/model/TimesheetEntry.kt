package com.example.timesheetapp.data.model

data class TimesheetEntry(
    val id: String = "",
    val projectId: String = "",
    val subcategoryId: String = "",
    val dailyHours: List<Double> = List(7) { 0.0 }, // Monday to Sunday
    val approved: Boolean = false,
    val approvedBy: String? = null
)
