package com.example.vext.ViewModel

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.vext.data.local.entity.AudioDes
import com.example.vext.model.Audio
import com.example.vext.data.local.repository.AudioRepository
import com.example.vext.jetaudio.player.services.JetAudioServiceHandler
import com.example.vext.jetaudio.player.services.JetAudioState
import com.example.vext.jetaudio.player.services.PlayerEvent
import com.example.vext.recorder.recorder.AndroidAudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private val audioDummy = Audio(
    "".toUri(), "", 0L, "", "", 0, "", 0, 0, false
)

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioServiceHandler: JetAudioServiceHandler,
    private val repository: AudioRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    //Player State
    @OptIn(SavedStateHandleSaveableApi::class)
    var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    @OptIn(SavedStateHandleSaveableApi::class)
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    @OptIn(SavedStateHandleSaveableApi::class)
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    @OptIn(SavedStateHandleSaveableApi::class)
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    @OptIn(SavedStateHandleSaveableApi::class)
    var currentSelectedAudio by savedStateHandle.saveable { mutableStateOf(audioDummy) }
    @OptIn(SavedStateHandleSaveableApi::class)
    var audioList by savedStateHandle.saveable {mutableStateOf(listOf<Audio>())}
    private var contentObserver: ContentObserver? = null

    //Ui State
    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    //Recorder State
    val recorder by lazy {
        AndroidAudioRecorder(context)
    }

    private fun saveAudioToLocal(audioDes: AudioDes) {
        viewModelScope.launch {
            repository.insertAudioFilesLocal(
                audioDes
            )
        }
    }

    init {
        loadAudioData()
        viewModelScope.launch {
            repository.logTest()
        }
    }

    init {
        viewModelScope.launch {
            audioServiceHandler.audioSate.collectLatest { mediaState ->
                when(mediaState) {
                    JetAudioState.Initial -> _uiState.value = UIState.Initial
                    is JetAudioState.Buffering -> calculateProgressValue(mediaState.progress)
                    is JetAudioState.Playing -> isPlaying = mediaState.isPlaying
                    is JetAudioState.Progress -> calculateProgressValue(mediaState.progress)
                    is JetAudioState.CurrentPlaying -> {
                        currentSelectedAudio = audioList[mediaState.mediaItemIndex]
                    }
                    is JetAudioState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UIState.Ready
                    }
                }
            }
        }
    }

    fun loadAudioData() {
        viewModelScope.launch {
            val audio = repository.getLocalAudioFiles() //get data
            audioList = audio
            setMediaItems()
            if(contentObserver == null) {
                contentObserver = context.contentResolver.registerObserver(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                ) {
                    loadAudioData()
                }
            }
        }
    }

    private fun setMediaItems() {
        audioList.map { audio ->
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artist)
                        .setDisplayTitle(audio.title)
                        .setSubtitle(audio.displayName)
                        .build()
                )
                .build()
        }.also {
            audioServiceHandler.setMediaItemList(it)
        }
    }

    private suspend fun deleteAudio (audio: Audio){
        repository.deleteAudioFilesLocal(audio)
    }

    override fun onCleared() {
        viewModelScope.launch {
            audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        contentObserver?.let {
            context.contentResolver.unregisterContentObserver(it)
        }
        super.onCleared()
    }

    //Events Handling
    fun onUIEvents(uiEvents: UIEvents) = viewModelScope.launch {
        when (uiEvents) {
            UIEvents.Backward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Backward)
            UIEvents.Forward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Forward)
            UIEvents.SeekToNext -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)

            is UIEvents.PlayPause -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.PlayPause
                )
            }

            is UIEvents.SeekTo -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo,
                    seekPosition = ((duration * uiEvents.position) / 100f).toLong()
                )
            }

            is UIEvents.SelectedAudioChange -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SelectedAudioChange,
                    selectAudioIndex = uiEvents.index
                )
            }

            is UIEvents.UpdateProgress -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.UpdateProgress(
                        uiEvents.newProgress
                    )
                )
                progress = uiEvents.newProgress
            }

            is UIEvents.DeleteSelectedAudios -> {
                deleteAudio(uiEvents.audio)
            }
        }
    }

    fun onRecordEvents(recordEvents: RecordEvents) = viewModelScope.launch {
        when (recordEvents) {
            is RecordEvents.SaveRecordingToLocal -> {
                saveAudioToLocal(recordEvents.audio)
            }
        }
    }



    //Utils
    private fun calculateProgressValue(currentProgress: Long) {
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat() / duration.toFloat()) * 100f)
            else 0f
        progressString = formatDuration(currentProgress)
    }

    @SuppressLint("DefaultLocale")
    fun formatDuration(duration: Long): String {
        val minute = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minute, seconds)
    }
}



private fun ContentResolver.registerObserver(
    uri: Uri,
    observer: (selfChange: Boolean) -> Unit
): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}


//Events, States Defined

sealed class RecordEvents {
    data class SaveRecordingToLocal(val audio: AudioDes) : RecordEvents()
}

sealed class UIEvents {
    data class DeleteSelectedAudios(val audio: Audio) : UIEvents()
    object PlayPause : UIEvents()
    data class SelectedAudioChange(val index: Int) : UIEvents()
    data class SeekTo(val position: Float) : UIEvents()
    object SeekToNext : UIEvents()
    object Backward : UIEvents()
    object Forward : UIEvents()
    data class UpdateProgress(val newProgress: Float) : UIEvents()
}

sealed class UIState {
    object Initial : UIState()
    object Ready : UIState()
}