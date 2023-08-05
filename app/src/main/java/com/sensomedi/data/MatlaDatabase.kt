package com.sensomedi.data

import android.content.Context
import androidx.room.*

@Database(entities = [MatlaData::class, User::class], version = 1)
@TypeConverters(Converters::class)
abstract class MatlaDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao
    abstract fun userDao(): UserDao

    companion object {
        private var instance: MatlaDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MatlaDatabase? {
            if (instance == null) {
                synchronized(MatlaDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MatlaDatabase::class.java,
                        "matla-database"
                    ).fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }
    }

}
