package com.example.vext.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vext.data.local.entity.AudioDes


@Database(entities = [AudioDes::class], version = 1)
abstract class AppDatabase : RoomDatabase(

) {
    abstract fun audioDao(): AudioDao

    companion object {
        const val DATABASE_NAME = "mydb"
        @Volatile
        private var INSTANCE: RoomDatabase? = null

        fun getInstance(
            context: Context
        ): RoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}