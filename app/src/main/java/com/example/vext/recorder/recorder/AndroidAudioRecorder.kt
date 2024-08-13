package com.example.vext.recorder.recorder

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.compose.runtime.mutableLongStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AndroidAudioRecorder @Inject constructor(
    private val context: Context,
    private val reloadData: () -> Unit
): AudioRecorder {

    private var recorder: MediaRecorder? = null

    private var tempFile = File(context.cacheDir, "temp_audio.mp3")
    private var targetUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val amplitudes = mutableListOf<Float>()
    private val scope = CoroutineScope(Dispatchers.Main)

    private val timerHandler = TimerHandler()
    var recordingTime = mutableLongStateOf(0L)

    var isPaused: Boolean = false
    var isStop: Boolean = false


    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start() {
        isStop = false
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(16*44100)
            setAudioSamplingRate(44100)
            setOutputFile(tempFile.absolutePath)

            prepare()
            start()

            recorder = this
        }

        timerHandler.start()

        scope.launch {
            while (recorder != null) {
                delay(10L)
                if(!isPaused) {
                    recordingTime.longValue = timerHandler.milliseconds
                }
            }
        }

    }

    override fun stop(filename: String) {
        isStop = true
        recorder?.stop()
        recorder?.reset()
        recorder = null
        scope.launch {
            saveAudioFile(filename)
            delay(1000L)
            reloadData()
        }
        recordingTime.longValue = 0
    }

    override fun getAmplitude(): Int {
        return recorder?.maxAmplitude ?: 0
    }

    fun cancel() {
        isStop = true
        recordingTime.longValue = 0
        recorder?.stop()
        recorder?.reset()
        recorder = null
        tempFile.delete()
    }

    fun pause() {
        recorder?.pause()
        isPaused = true
    }

    fun resume() {
        isPaused = false
        recorder?.resume()

    }

    private fun saveAudioFile(filename: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
        }

        val uri = resolver.insert(targetUri, contentValues)

        uri?.let {
            val outputStream = resolver.openOutputStream(it)
            val inputStream = tempFile.inputStream()

            copyStream(inputStream, outputStream)

            inputStream.close()
            outputStream?.close()
        }
        tempFile.delete()
    }

    private fun saveAmplitudesToFile(filename: String){
        val file = File(context.filesDir, "$filename.amplitudes")
        file.writeText(amplitudes.joinToString(","))
    }

    private fun copyStream(input: InputStream, output: OutputStream?) {
        val buffer = ByteArray(1024)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            output?.write(buffer, 0, read)
        }
    }
}

class TimerHandler {
    var milliseconds = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            milliseconds += 10
            handler.postDelayed(this, 10)
        }
    }

    fun start() {
        handler.postDelayed(runnable, 10)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        milliseconds = 0L
    }
}