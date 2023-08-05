package com.sensomedi.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {

    @Insert
    fun insert(matla: MatlaData)

//    @Delete
//    fun delete(id: String)

    @Query("SELECT * FROM MatlaData")
    fun getAll(): List<MatlaData>

    @Query("DELETE FROM MatlaData WHERE date = :id")
    fun deleteData(id: String)

}