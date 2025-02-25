package com.example.sggsiet.dataclasses

data class LeaveRequest(
    val studentName: String = "",
    val studentEmail: String = "",
    val studentMobile: String = "",
    val studentDept: String = "",
    val duration: String = "",
    val reason: String = "",
    val status: String = "pending"
)