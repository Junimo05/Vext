package com.example.vext.data.local.repository

import com.example.vext.data.local.model.Audio
import com.example.vext.data.local.services.AudioService.AudioLocalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val audioLocalService: AudioLocalService,
) {
    suspend fun getAllAudioFilesLocal() = withContext(Dispatchers.IO){
        audioLocalService.getAllAudioFiles()
    }

    suspend fun deleteAudioFilesLocal (audio: Audio) = withContext(Dispatchers.IO){
        try {
            audioLocalService.deleteAudioFile(audio)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}