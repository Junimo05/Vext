package com.example.vext.ui.audio

import android.annotation.SuppressLint
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun AudioFingerprintDisplay(
    amplitudes: MutableList<Float>,
    modifier: Modifier = Modifier
) {
    val isPlaying by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        if (isPlaying) 1f else 0f,
        animationSpec = tween(durationMillis = 2000), label = ""
    )

    //Draw
    val numberOfMainMarkers = 5
    val numberOfSubMarkers = 3
    var sizeCanvas by remember { mutableStateOf(IntSize.Zero) }

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
//                val columnWidth = size.width / 50
//                val amplitude = size.height / 2
//                val amplitudesCopy = amplitudeData.getLast(50)
//
//                amplitudesCopy.forEachIndexed { index, amp ->
//                    val rectX = index * columnWidth
//                    val rectY = amplitude - amp / 32768.0f * amplitude
//                    val rectHeight = 2 * amp / 32768.0f * amplitude // height of the rectangle is twice the distance from rectY to the bottom of the canvas
//
//                    drawRect(
//                        color = Color.Black,
//                        topLeft = Offset(rectX, rectY),
//                        size = androidx.compose.ui.geometry.Size(width = columnWidth, height = rectHeight)
//                    )
//                }

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