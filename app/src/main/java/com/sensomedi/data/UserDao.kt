package com.sensomedi.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM User")
    fun getUser(): List<User>

//    @Delete
//    fun delete(id: String)


    @Query("DELETE FROM User")
    fun deleteUser()

}