package com.example.vext

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vext.jetaudio.player.services.JetAudioService
import com.example.vext.ui.audio.AudioViewModel
import com.example.vext.ui.audio.Home
import com.example.vext.ui.audio.UIEvents
import com.example.vext.ui.theme.VextTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val audioViewModel: AudioViewModel by viewModels()
    private var isServiceRunning = false

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            PackageManager.PERMISSION_GRANTED
        )
        setContent {
            VextTheme {
                requestRuntimePermission()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    Home(
                        progress = audioViewModel.progress,
                        onProgress = { audioViewModel.onUIEvents(UIEvents.SeekTo(it)) },
                        isAudioPlaying = audioViewModel.isPlaying,
                        audioList = audioViewModel.audioList,
                        currentPlayingAudio = audioViewModel.currentSelectedAudio,
                        onStart = {
                            audioViewModel.onUIEvents(UIEvents.PlayPause)
                        },
                        onItemClick = {
                            audioViewModel.onUIEvents(UIEvents.SelectedAudioChange(it))
                            startService()
                        },
                        onNext = {
                            audioViewModel.onUIEvents(UIEvents.SeekToNext)
                        },
                        context = this,
                        reloadData = {
                            audioViewModel.loadAudioData()
                        }
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestRuntimePermission(){
        when{
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                audioViewModel.loadAudioData()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO) -> {

            } else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                    1
                )
            }
        }
    }

    private fun startService() {
        if (!isServiceRunning) {
            val intent = Intent(this, JetAudioService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isServiceRunning = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        requestRuntimePermission()
        audioViewModel.loadAudioData()
    }
}

