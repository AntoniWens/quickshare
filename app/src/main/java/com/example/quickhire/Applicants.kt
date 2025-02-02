package com.example.quickhire

import java.io.Serializable

data class Applicants(
    val id: Long = 0,
    val desc: String = "",
    val userName: String = "",
    val uid: String = "",
    val jobId: Long = 0
): Serializable
