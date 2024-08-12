package com.example.vext.recorder.recorder

interface AudioRecorder {
    fun start()
    fun stop(filename: String)

    fun getAmplitude(): Int
}