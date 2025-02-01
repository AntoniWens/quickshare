package com.example.quickhire

data class User(
    val email: String = "",
    val uid: String = "",
    val fullName: String = "",
    val detailBusiness: String = "",
    val location: String = "",
    val phone: String = "",
    val role: Int = 0
)
