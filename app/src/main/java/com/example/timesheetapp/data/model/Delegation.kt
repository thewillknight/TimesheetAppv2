package com.example.timesheetapp.data.model

data class Delegation(
    val canApprove: Boolean = true,
    val addedAt: String = "",
    val addedBy: String = "",
    val submitterId: String = ""
)