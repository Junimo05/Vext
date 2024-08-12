package com.example.vext.ui.audio

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.vext.data.local.model.Audio
import com.example.vext.recorder.recorder.AndroidAudioRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun Home(
    navController: NavController,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Audio,
    audioList: List<Audio>,
    deleteAudio: (Audio) -> Unit,
    onStart: () -> Unit,
    onAudioClick:(Int) -> Unit,
    onNext: () -> Unit,
    context: Context,
    ) {
    //Select Options
    var isInSelectionMode by remember {
        mutableStateOf(false)
    }
    val selectedItems = remember {
        mutableStateListOf<Audio>()
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
                         context = context,
                         selectedItems = selectedItems,
                         resetSelectionMode = resetSelectionMode,
                         deleteAudio = { items ->
                             items.forEach { item ->
                                    deleteAudio(item)
                             }
                         }
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
                        navController.navigate(
                            "record"
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ){
        LazyColumn(
            contentPadding = it
        ) {
            itemsIndexed(audioList){index, audio ->
                val isSelected = selectedItems.contains(audio)
                ListItem(
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            if (isInSelectionMode) {
                                if (isSelected) {
                                    selectedItems.remove(audio)
                                } else {
                                    selectedItems.add(audio)
                                }
                            } else {
                                onAudioClick(index)
                            }
                        },
                        onLongClick = {
                            if (isInSelectionMode) {
                                if (isSelected) {
                                    selectedItems.remove(audio)
                                } else {
                                    selectedItems.add(audio)
                                }
                            } else {
                                isInSelectionMode = true
                                selectedItems.add(audio)
                            }
                        }
                    ),
                    headlineContent = {
                        AudioItem(
                            index = index,
                            audio = audio,
                            isAudioPlaying = isAudioPlaying,
                            currentPlayingAudio = currentPlayingAudio,
                            onAudioClick = onAudioClick,
                            progress = progress,
                            onProgress = onProgress,
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
    }
}

@Composable
@Preview(showBackground = true)
fun HomePreview(){

}
