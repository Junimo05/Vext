package com.example.vext.di

import android.content.Context
import androidx.room.RoomDatabase
import com.example.vext.data.AppDatabase
import com.example.vext.data.AudioDao
import com.example.vext.data.local.services.AudioService.AudioLocalService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): RoomDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAudioLocalService(
        @ApplicationContext context: Context,
        audioDao: AudioDao
    ): AudioLocalService {
        return AudioLocalService(context, audioDao)
    }

    @Provides
    @Singleton
    fun provideAudioDao(
        database: RoomDatabase
    ): AudioDao {
        return (database as AppDatabase).audioDao()
    }


}