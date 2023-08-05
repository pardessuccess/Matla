package com.sensomedi.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class MA(
    @PrimaryKey val date: String,
    val matlaData: List<Int>
) : Serializable
