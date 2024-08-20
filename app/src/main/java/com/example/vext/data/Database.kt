package com.example.vext.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.example.vext.data.local.entity.AudioDes


@Database(
    entities = [AudioDes::class],
    version = 3,
    exportSchema = true,
//    autoMigrations = [
//        AutoMigration(
//            from = 1,
//            to = 2,
//            spec = AppDatabase.MyAutoMigration::class
//        )
//    ]
)
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
                )
                    .fallbackToDestructiveMigration() //delete if want to use auto migration
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    @RenameTable(fromTableName = "User", toTableName = "AppUser")
    class MyAutoMigration : AutoMigrationSpec {

    }
}