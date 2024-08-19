package com.example.vext.data.local.repository

import android.content.Context
import android.media.CamcorderProfile.getAll
import android.util.Log
import androidx.core.net.toUri
import com.example.vext.data.local.entity.AudioDes
import com.example.vext.data.local.entity.toAudio
import com.example.vext.model.Audio
import com.example.vext.data.local.services.AudioService.AudioLocalService
import com.example.vext.utils.checkAudioExistsInMediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AudioRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioLocalService: AudioLocalService,
) {
    private val TAG = "AudioRepository"

    suspend fun getAllAudioFilesLocal() = withContext(Dispatchers.IO){
        try {
            audioLocalService.getAllAudioFiles()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "getAllAudioFilesLocalError: ${e.message}")
            emptyList()
        }
    }

    suspend fun getLocalAudioFiles(): List<Audio> {
        val audioDesList = audioLocalService.getAllAudioData()

        return audioDesList.map { audioDes ->
            val audio = audioDes.toAudio()

            val fileExists = checkAudioExistsInMediaStore(context, audioDes.id.toUri())

            if (fileExists) {
                audio
            } else {
                deleteAudioFilesLocal(audioDes.id)
                null
            }
        }.filterNotNull()
    }

    suspend fun deleteAudioFilesLocal (audioId: String) = withContext(Dispatchers.IO){
        try {
            audioLocalService.deleteAudioById(audioId)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "deleteAudioFilesLocalError: ${e.message}")
        }
    }

    suspend fun deleteAudioFilesLocal (audio: Audio) = withContext(Dispatchers.IO){
        try {
            audioLocalService.deleteAudioFile(audio)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "deleteAudioFilesLocalError: ${e.message}")
        }
    }

    suspend fun insertAudioFilesLocal(audio: AudioDes) = withContext(Dispatchers.IO){
        try {
            audioLocalService.insertAudio(audio)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "insertAudioFilesLocalError: ${e.message}")
        }
    }

    suspend fun deleteAllDataAudio() = withContext(Dispatchers.IO){
        try {
            audioLocalService.deleteAllAudioData()
        }catch (ex: Exception){
            ex.printStackTrace()
            Log.e(TAG, "deleteAllDataAudioError: ${ex.message}")
        }
    }

    suspend fun logTest(){
        val audioList = audioLocalService.getAllAudioData()
        audioList.forEach { audio ->
            Log.d("AudioInfo", "" +
                    "Audio name: ${audio.audioName}, " +
                    "Audio path: ${audio.audioPath}, " +
                    "Audio size: ${audio.audioSize}, " +
                    "Audio type: ${audio.audioType}, " +
                    "Audio channel: ${audio.audioChannel}, " +
                    "Audio bitrate: ${audio.audioBitrate}, " +
                    "Audio sample rate: ${audio.audioSampleRate}, " +
                    "Audio created: ${audio.audioCreated}, " +
                    "Audio added: ${audio.audioAdded}, " +
                    "Audio removed: ${audio.audioRemoved}, " +
                    "Audio waveform processed: ${audio.audioWaveformProcessed}, " +
                    "Audio bookmarked: ${audio.audioBookmarked}," +
                    "Audio duration: ${audio.audioDuration}"
            )
        }
    }
}