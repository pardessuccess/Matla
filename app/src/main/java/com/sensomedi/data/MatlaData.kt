package com.sensomedi.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class MatlaData(
    @PrimaryKey val date: Long,
    val matlaData: List<Int>
) : Serializable
