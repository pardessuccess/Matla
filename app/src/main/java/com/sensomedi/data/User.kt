package com.sensomedi.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey val email: String = "",
    val age: Int = 0,
    val gender: String = "",
    val height: Int = 0,
    val weight: Int = 0
) : Serializable
