package com.sensomedi.data

import java.io.Serializable

data class Temporary(
    val date: Long,
    val matlaData: List<Int>
) : Serializable