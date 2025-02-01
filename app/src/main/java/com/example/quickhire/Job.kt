package com.example.quickhire

import java.io.Serializable

data class Job(
    val jobId: Long,
    val jobTitle: String = "",
    val companyName: String = "",
    val salary: String = "",
    val location: String = "",
    val jobDesc: String = "",
    val status: Int = 0,
    val uid: String = "",
    val skill: List<String>,
): Serializable
