package com.example.vext.ui.audio

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vext.data.local.model.Audio

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioItem(
    audio: Audio,
    currentPlayingAudio: Audio,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row (
            modifier = modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = audio.displayName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Clip,
                    maxLines = 1,
                    modifier = if(audio == currentPlayingAudio) Modifier.basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 1000,
                    ) else Modifier,
                    color = if(audio == currentPlayingAudio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = audio.artist,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Clip,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1
                )
            }
            Text(
                text = timeStampToDuration(audio.duration.toLong()),
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}