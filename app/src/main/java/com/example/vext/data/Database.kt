package com.example.vext.data

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper


@Database(entities = [AudioDes::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun audioDao(): AudioDao

    companion object {
        const val DATABASE_NAME = "audio_db"
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
                    RoomDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}