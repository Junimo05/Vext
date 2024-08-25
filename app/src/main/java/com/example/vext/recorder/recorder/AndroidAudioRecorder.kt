package com.example.vext.recorder.recorder

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    private var audioId: String = ""

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

    override suspend fun stop(filename: String) {
        isStop.value = true
        isPaused.value = false
        isRecording.value = false
        timerHandler.stop()
        recorder?.stop()
        suspendCoroutine { continuation ->
            scope.launch {
                saveRecordingFile(filename)
                continuation.resume(Unit)
            }
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
    private fun saveRecordingFile(filename: String){
//        val uniqueFilename = generateUniqueFileName(filename)
        val audioUri = saveAudioFile(filename)
        createdTime = System.currentTimeMillis()
        savePath = audioUri.toString()
        fileSize = tempFile.length()
        this.filename = filename
//        saveAmplitudesToFile(this.filename)
        tempFile.delete()
    }

    override fun toItem(): AudioDes {
        val audioFilename = this.filename // atr created
        val audioDuration = this.recordingTime.longValue //atr created
        val audioPath = this.savePath //atr created
        val audioCreated = this.createdTime //atr created
        var audioBitrate: Int = 0
        var audioSampleRate: Int = 0
        val audioSize: Long = fileSize
        var audioChannel: Int = 0
        var audioWaveformProcessed: Boolean = false

        if (audioFilename == "" || audioPath == "") {
            throw IllegalStateException("Audio file not saved")
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val metrics = recorder?.metrics
            audioBitrate = metrics?.getInt(MediaRecorder.MetricsConstants.AUDIO_BITRATE) ?: 0
            audioSampleRate = metrics?.getInt(MediaRecorder.MetricsConstants.AUDIO_SAMPLERATE) ?: 0
            audioChannel = metrics?.getInt(MediaRecorder.MetricsConstants.AUDIO_CHANNELS) ?: 0

        }
        if(amplitudes.isNotEmpty()){
            audioWaveformProcessed = true
        }

        //check null values

        return AudioDes(
            audioName = audioFilename,
            audioDuration = audioDuration,
            audioPath = audioPath,
            audioCreated = audioCreated,
            audioAdded = System.currentTimeMillis(),
            audioRemoved = 0L,
            audioSize = audioSize,
            audioType = "audio/mp3",
            audioChannel = audioChannel,
            audioBitrate = audioBitrate,
            audioSampleRate = audioSampleRate,
            audioWaveformProcessed = audioWaveformProcessed,
            audioFavorite = false
        )
    }

    fun clearRecorder(){
        filename = ""
        savePath = ""
        createdTime = 0L
        fileSize = 0L
        amplitudes.clear()
        timerHandler.clear()
        recorder?.reset()
        recorder = null
    }
    private fun saveAudioFile(filename: String): Uri {
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
        return uri ?: Uri.EMPTY
    }

    private fun saveAmplitudesToFile(filename: String){
        val file = File(context.filesDir, "$filename.amplitudes")
        file.writeText(amplitudes.joinToString(","))
    }

//    private fun generateUniqueFileName(originalName: String): String {
//        var uniqueName = originalName
//        var counter = 1
//
//        while (checkAudioNameExistsInMediaStore(context, uniqueName)) {
//            uniqueName = "$originalName (${counter++})"
//        }
//        return uniqueName
//    }

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
    }

    fun clear(){
        milliseconds = 0L
    }
}