package com.example.vext.recorder.recorder

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.vext.data.local.entity.AudioDes
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
): AudioRecorder {

    private var recorder: MediaRecorder? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private var tempFile = File(context.cacheDir, "temp_audio.mp3")
    private var targetUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val amplitudes = mutableListOf<Float>()
    private val timerHandler = TimerHandler()
    var recordingTime = mutableLongStateOf(0L)

    private var filename: String = ""
    private var savePath: String = ""
    private var createdTime: Long = 0L
    private var fileSize: Long = 0L

    var isRecording: MutableState<Boolean> = mutableStateOf(false)
    var isPaused: MutableState<Boolean> = mutableStateOf(false)
    var isStop: MutableState<Boolean> = mutableStateOf(true)


    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start() {
        isStop.value = false
        isPaused.value = false
        isRecording.value = true
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
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
                if(!isPaused.value) {
                    recordingTime.longValue = timerHandler.milliseconds
                }
            }
        }

    }

    override fun stop(filename: String) {
        isStop.value = true
        isPaused.value = false
        isRecording.value = false
        timerHandler.stop()
        recorder?.stop()
        this.filename = filename
        this.fileSize = tempFile.length()
        scope.launch {
            saveAudioFile(filename)
            createdTime = System.currentTimeMillis()
            delay(1000L)
//            reloadData()
        }
    }

    override fun getAmplitude(): Int {
        return recorder?.maxAmplitude ?: 0
    }

    override fun cancel() {
        isStop.value = true
        isPaused.value = false
        isRecording.value = false
        timerHandler.stop()
        recordingTime.longValue = timerHandler.milliseconds
        recorder?.stop()
        recorder?.reset()
        tempFile.delete()
        recorder = null
    }

    override fun pause() {
        isPaused.value = true
        timerHandler.pause()
        recorder?.pause()
    }

    override fun resume() {
        isPaused.value = false
        timerHandler.resume()
        recorder?.resume()

    }

    override fun toItem(): AudioDes {
        var audio_filename = this.filename
        var audio_duration = this.recordingTime.longValue
        var audio_path = this.savePath
        var audio_created = this.createdTime
        var audio_bitrate: Int = 0
        var audio_sample_rate: Int = 0
        var audio_size: Long = fileSize
        var audio_channel: Int = 0
        var audio_waveform_processed: Boolean = false
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val metrics = recorder?.metrics
            audio_bitrate = metrics?.getInt(MediaRecorder.MetricsConstants.AUDIO_BITRATE) ?: 0
            audio_sample_rate = metrics?.getInt(MediaRecorder.MetricsConstants.AUDIO_SAMPLERATE) ?: 0
            audio_channel = metrics?.getInt(MediaRecorder.MetricsConstants.AUDIO_CHANNELS) ?: 0

        }
        if(amplitudes.isNotEmpty()){
            audio_waveform_processed = true
        }
        return AudioDes(
            audioName = audio_filename,
            audioDuration = audio_duration,
            audioPath = audio_path,
            audioCreated = audio_created,
            audioAdded = System.currentTimeMillis(),
            audioRemoved = 0L,
            audioSize = audio_size,
            audioType = "audio/mp3",
            audioChannel = audio_channel,
            audioBitrate = audio_bitrate,
            audioSampleRate = audio_sample_rate,
            audioWaveformProcessed = audio_waveform_processed,
            audioBookmarked = false
        )
    }
   fun clearRecorder(){
        filename = ""
        savePath = ""
        createdTime = 0L
        fileSize = 0L
        amplitudes.clear()
        recorder?.reset()
        recorder = null
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
            this.savePath = uri.toString()
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

    fun pause() {
        handler.removeCallbacks(runnable)
    }

    fun resume() {
        handler.postDelayed(runnable, 10)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        milliseconds = 0L
    }
}