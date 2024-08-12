package com.example.vext.ui.audio

import kotlin.math.floor

fun timeStampToDuration(position: Long): String{
    val totalSec = floor(position / 1E3).toInt()
    val minutes = totalSec / 60
    val remainingSec = totalSec - minutes * 60
    return if(position < 0) "--.--"
    else if (remainingSec < 10) "$minutes:0$remainingSec"
    else "$minutes:$remainingSec"
}

fun timeStampRecorder_Tick(position: Long): String{
    val totalSec = floor(position / 1E3).toInt()
    val minutes = totalSec / 60
    val remainingSec = totalSec - minutes * 60
    val millis = (position % 1000) / 10 // Get the first two digits of the milliseconds
    return if(position < 0) "00:00.00"
    else if (remainingSec < 10) "$minutes:0$remainingSec.$millis"
    else "$minutes:$remainingSec.$millis"
}