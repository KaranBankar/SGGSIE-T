package com.example.sggsiet.dataclasses

data class LeaveApplication(
    val name: String = "",
    val email: String = "",
    val reason: String = "",
    val status: String = "Pending",
    val applicationDate: String = "", // Date of leave
    val mobile: String = "",
    val submissionDate: String = "" // Timestamp of submission
)