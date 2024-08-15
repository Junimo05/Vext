package com.example.vext.ui.audio

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.vext.recorder.recorder.AndroidAudioRecorder
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun AudioFingerprintDisplay(
    isRecording: MutableState<Boolean>,
    recorder: AndroidAudioRecorder,
    modifier: Modifier = Modifier
) {
    val isPlaying by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        if (isPlaying) 1f else 0f,
        animationSpec = tween(durationMillis = 2000), label = ""
    )

    //Draw
    val amplitudes = remember { mutableStateOf(listOf<Float>()) }
    val numberOfMainMarkers = 5
    val numberOfSubMarkers = 3
    var sizeCanvas by remember { mutableStateOf(IntSize.Zero) }

    fun addAmplitudeData(amp: Float) {
        amplitudes.value += amp
        if(amplitudes.value.size > 100) {
            amplitudes.value = amplitudes.value.drop(1)
        }
    }

    fun clearAmplitudeData() {
        amplitudes.value = listOf()
    }

    LaunchedEffect(key1 = isRecording.value, key2 = recorder.isPaused.value) {
        while (isRecording.value && !recorder.isPaused.value) {
            val amplitude = recorder.getAmplitude().toFloat()
            addAmplitudeData(amplitude) //save amplitude for display
            recorder.amplitudes.add(amplitude) //save amplitude to file
            delay(100)
        }
        if(!isRecording.value) {
            clearAmplitudeData()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight(0.7F)
            .fillMaxWidth()
            .onGloballyPositioned { layoutCoordinates ->
                sizeCanvas = layoutCoordinates.size
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .background(Color.Gray)
                .padding(4.dp)
                .fillMaxWidth()
                .height(250.dp),
            onDraw = {
                val mainMarkerSpacing = (size.width.toDp() / (numberOfMainMarkers - 1)).toPx() // Spacing between markers
                val subMarkerSpacing = mainMarkerSpacing / (numberOfSubMarkers + 1) // Spacing between sub-markers

                // Draw the time markers
                for (i in 0 until numberOfMainMarkers) {
                    val xPosition = i * mainMarkerSpacing
                    drawLine(
                        color = Color.White,
                        start = Offset(xPosition, 0f),
                        end = Offset(xPosition, size.height/15),
                        strokeWidth = 1.dp.toPx(),
                    )

                    if (i < numberOfMainMarkers - 1) {
                        for (j in 1..numberOfSubMarkers) {
                            val subXPosition = xPosition + j * subMarkerSpacing
                            drawLine(
                                color = Color.White,
                                start = Offset(subXPosition, 0f),
                                end = Offset(subXPosition, size.height/40),
                                strokeWidth = 1.dp.toPx(),
                            )
                        }
                    }
                }

                //Draw WaveForm

                val columnWidth = size.width / 100
                val amplitudeBase = size.height / 2

                amplitudes.value.forEachIndexed { index, amp ->
                    val rectX = index * columnWidth
                    val normalizedAmp = amp / 32768.0f * amplitudeBase
                    val rectY = amplitudeBase - normalizedAmp
                    val rectHeight = normalizedAmp // height of the rectangle is the distance from rectY to the bottom of the canvas

                    // Draw rectangle extending upwards
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(rectX, rectY),
                        size = Size(width = columnWidth, height = rectHeight)
                    )

                    // Draw rectangle extending downwards
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(rectX, amplitudeBase),
                        size = Size(width = columnWidth, height = rectHeight)
                    )
                }

                // Draw the progress line
                drawLine(
                    color = Color.Red,
                    start = Offset(size.width/2 - 1, 0f),
                    end = Offset(size.width/2, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
        )
    }
}