package com.example.vext.ui.audio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vext.data.local.model.Audio
import java.util.concurrent.TimeUnit

@Composable
fun BottomBarLayer(
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
){
    BottomAppBar (
        content = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxSize()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    PlayerIconItem(
                        icon = Icons.Default.MusicNote,
                        borderStroke = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.surface
                        ),
                        onClick = {
                            // TODO:
                        }
                    )
                    MediaPlayer(
                        isAudioPlaying = isAudioPlaying,
                        onStart = onStart,
                        onNext = onNext,
                        audio = audio
                    )
                    Slider(
                        value = progress,
                        onValueChange = {onProgress(it)},
                        valueRange = 0f..100f,
                        modifier = Modifier.weight(1f)
                    )
                    TimeStampMedia(
                        progress = progress,
                        duration = audio.duration.toLong()
                    )
                }
            }
        }
    )
}

@Composable
fun TimeStampMedia(
    progress: Float,
    duration: Long
){
    val progressMillis = (progress / 100f * duration).toLong()
    val progressMinutes = TimeUnit.MILLISECONDS.toMinutes(progressMillis)
    val progressSeconds = TimeUnit.MILLISECONDS.toSeconds(progressMillis) - TimeUnit.MINUTES.toSeconds(progressMinutes)

    val stringMinute = progressMinutes.toString()
    val stringSec = if(progressSeconds < 10) "0$progressSeconds" else progressSeconds.toString()
    Surface {
        Text(
            text = "$stringMinute:$stringSec/${timeStampToDuration(duration)}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(4.dp)
        )
    }
}


@Composable
fun ArtistInfo(
    modifier: Modifier = Modifier,
    audio: Audio
){
    Row (
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        PlayerIconItem(
            icon = Icons.Default.MusicNote,
            borderStroke = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ),
            onClick = { }
        )
        Spacer(modifier = Modifier.size(4.dp))
        Column {
            Text(text = audio.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Clip,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = audio.artist,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PlayerIconItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    borderStroke: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
){
    Surface(
        shape = CircleShape,
        border = borderStroke,
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .then(modifier),
        contentColor = color,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center
        ){
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

@Composable
fun MediaPlayer(
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    audio: Audio?
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp)
    ){
        PlayerIconItem(
            icon = if(isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
        ) {
            if (audio != null) {
                if(audio.title.isNotEmpty()){
                    onStart()
                }
            }
        }
        Spacer(modifier = Modifier.size(4.dp))
        Icon(
            imageVector = Icons.Default.SkipNext,
            modifier = Modifier.clickable {
                onNext()
            },
            contentDescription = null
        )
    }
}