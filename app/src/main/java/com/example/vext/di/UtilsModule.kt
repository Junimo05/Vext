package com.example.vext.di

import android.content.Context
import com.example.vext.utils.Pref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {
    @Singleton
    @Provides
    fun providePref(
        @ApplicationContext context: Context
    ): Pref {
        return Pref(context)
    }

}