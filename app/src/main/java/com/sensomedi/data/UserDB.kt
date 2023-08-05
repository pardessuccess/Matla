package com.sensomedi.data

data class UserDB(
    val age: Int = 0,
    val email: String = "",
    val gender: String = "",
    val height: Int = 0,
    val matla: List<MatlaData>? = listOf(),
    val weight: Int = 0
)
