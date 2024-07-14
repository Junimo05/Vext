package com.example.vext.ui.audio

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vext.data.local.model.Audio
import com.example.vext.recorder.recorder.AndroidAudioRecorder

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Home(
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Audio,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick:(Int) -> Unit,
    onNext: () -> Unit,
    context: Context,
    reloadData: () -> Unit
    ) {
    val recorder by lazy {
        AndroidAudioRecorder(context = context, reloadData = reloadData)
    }
    var showAlertDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isRecording by mutableStateOf(false)

    var isInSelectionMode by remember {
        mutableStateOf(false)
    }
    val selectedItems = remember {
        mutableStateListOf<Long>()
    }

    val resetSelectionMode = {
        isInSelectionMode = false
        selectedItems.clear()
    }

    BackHandler(
        enabled = isInSelectionMode,
    ) {
        resetSelectionMode()
    }

    LaunchedEffect(
        key1 = isInSelectionMode,
        key2 = selectedItems.size,
    ) {
        if (isInSelectionMode && selectedItems.isEmpty()) {
            isInSelectionMode = false
        }
    }

    Scaffold(
        topBar = {
                 if(isInSelectionMode){
                     SelectionModeTopAppBar(
                         selectedItems = selectedItems,
                         resetSelectionMode = resetSelectionMode
                     )
                 }else {
                     CenterAlignedTopAppBar(
                         title = {
                             Text(
                                 text = "Audios",
                             )
                         },
                     )
                 }
        },
        bottomBar = {
            BottomBarLayer(
                progress = progress,
                onProgress = onProgress,
                audio = currentPlayingAudio,
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                   isRecording = !isRecording
                        if(isRecording) recorder.start()
                        else {
                            recorder.pause()
                            showAlertDialog = true
                        }
                },
                containerColor = Color(0xFFFF9800),
                shape = CircleShape,
            ) {
                if (isRecording) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(text = timeStampToDuration(recorder.recordingTime.value))
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ){
        LazyColumn(
            contentPadding = it
        ) {
            itemsIndexed(audioList){index, audio ->
                val isSelected = selectedItems.contains(audio.id)
                ListItem(
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            if (isInSelectionMode) {
                                if (isSelected) {
                                    selectedItems.remove(audio.id)
                                } else {
                                    selectedItems.add(audio.id)
                                }
                            } else {
                                onItemClick(index)
                            }
                        },
                        onLongClick = {
                            if (isInSelectionMode) {
                                if (isSelected) {
                                    selectedItems.remove(audio.id)
                                } else {
                                    selectedItems.add(audio.id)
                                }
                            } else {
                                isInSelectionMode = true
                                selectedItems.add(audio.id)
                            }
                        }
                    ),
                    headlineContent = {
                        AudioItem(
                            audio = audio,
                            currentPlayingAudio = currentPlayingAudio,
                        )
                        Spacer(modifier = Modifier
                            .size(1.dp)
                            .background(MaterialTheme.colorScheme.onSurface)
                        )
                    },
                    leadingContent = {
                        if (isInSelectionMode) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                            )
                        }
                    }
                )

            }
        }
        if(showAlertDialog){
            StopAlertDialog(
                onSaveRequest = {filename ->
                    showAlertDialog = false
                    recorder.stop(filename)
                },
                onDismissRequest = {
                    showAlertDialog = false
                }
            )
        }
    }
}

@Composable
fun StopAlertDialog(
    onDismissRequest: () -> Unit,
    onSaveRequest: (String)-> Unit
){
    var fileName = rememberSaveable {
        mutableStateOf("AudioFile")
    }
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(text = "Save Record") },
        text = {
            TextField(
                value = fileName.value,
                onValueChange = { fileName.value = it },
                label = { Text(text = "File Name") },
                maxLines = 1
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    fileName.value += ".mp3"
                    onSaveRequest(fileName.value)
                }
            ) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

