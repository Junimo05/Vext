package com.example.vext.recorder.recorder

import android.content.Context
import com.example.vext.data.local.entity.AudioDes

interface AudioRecorder {
    fun start()
    suspend fun stop(filename: String)
    fun cancel()
    fun pause()
    fun resume()

    fun toItem(): AudioDes

    fun getAmplitude(): Int
}