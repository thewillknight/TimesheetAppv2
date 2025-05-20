package com.example.timesheetapp.data.model

import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val email: String = "",
    val admin: Boolean = false,
    val firstName: String = "",
    val lastName: String = ""
)
