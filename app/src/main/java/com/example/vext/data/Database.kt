package com.example.vext.data

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.example.vext.data.local.entity.AudioDes


@Database(
    entities = [AudioDes::class],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = AppDatabase.Migrate3to4::class),
    ]
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
                   .addMigrations()
                   .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    @RenameColumn(tableName = "audio", fromColumnName = "audioBookmarked", toColumnName = "audioFavorite")
    class Migrate3to4 : AutoMigrationSpec
}