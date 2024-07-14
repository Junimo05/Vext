package com.example.vext.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Immutable
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import com.example.vext.R
import com.example.vext.jetaudio.player.notifications.JetAudioNotificationManager
import com.example.vext.jetaudio.player.services.JetAudioServiceHandler
import com.google.common.collect.ImmutableList
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()


    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .setTrackSelector(DefaultTrackSelector(context))
        .apply {
            setUseLazyPreparation(true)
        }
        .build()

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ): MediaSession {

        val likeButton = CommandButton.Builder()
            .setDisplayName("Like")
            .setIconResId(R.drawable.jerry2)
            .setSessionCommand(SessionCommand(SessionCommand.COMMAND_CODE_SESSION_SET_RATING))
            .build()

        return MediaSession.Builder(context, player)
            .setCustomLayout(ImmutableList.of(likeButton))
            .build()
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer
    ): JetAudioNotificationManager = JetAudioNotificationManager(context,exoPlayer)

    @Provides
    @Singleton
    fun provideServiceHandler(
        exoPlayer: ExoPlayer
    ): JetAudioServiceHandler = JetAudioServiceHandler(exoPlayer)

}