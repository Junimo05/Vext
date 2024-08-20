package com.example.vext.data.local.services.AudioService

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import com.example.vext.data.AudioDao
import com.example.vext.data.local.entity.AudioDes
import com.example.vext.model.Audio
import com.example.vext.utils.checkAudioExistsInMediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AudioLocalService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioDao: AudioDao,
){

    //Main Getting

    //File Getting
        private val TAG = "AudioLocalService"

        private val audioDataFormat: Array<String> = arrayOf(
            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.BITRATE,
        )

        //Audio Data Selection Clause
        private val audioDataSelectionClause: String? =
            "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ? AND ${MediaStore.Audio.Media.MIME_TYPE} NOT IN (?, ?, ?)"

        //Audio Data Selection Argument
        private val audioDataSelectionArg = arrayOf("1", "audio/amr", "audio/3gpp", "audio/aac")

        //Audio Data Sort Order
        private val audioDataSortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"
        @SuppressLint("Recycle")
        fun getAllAudioFiles(): List<Audio>{

            val audioList = mutableListOf<Audio>()

            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                audioDataFormat,
                audioDataSelectionClause,
                audioDataSelectionArg,
                audioDataSortOrder
            )

            cursor?.use {cursor ->
                val idColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                val artistColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                val dataColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                val titleColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)

                cursor.apply {
                    if(count == 0){
                        Log.e(TAG, "getCursorData: Cursor is Empty")
                    }else {
                        while (cursor.moveToNext()) {
                            val displayName = getString(displayNameColumn)
                            val id = getLong(idColumn)
                            val artist = getString(artistColumn)
                            val data = getString(dataColumn)
                            val duration = getInt(durationColumn)
                            val title = getString(titleColumn)
                            val uri = ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                            audioList += Audio(
                                uri, displayName, id, artist, data, duration, title, 0L, 0L, false
                            )
                        }
                    }
                }
            }

            return audioList
        }

        fun deleteAudioFile(audio: Audio){
            Log.e(TAG, "deleteAudioFile: Deleting Audio File with uri: ${audio.uri}")
            if(checkAudioExistsInMediaStore(context, audio.uri)){
                context.contentResolver.delete(audio.uri, null, null)
            }
        }

    //Database Getting
    suspend fun getAllAudioData() = audioDao.getAllAudio()

    suspend fun getAudioById(id: Int) = audioDao.getAudio(id)

    suspend fun deleteAudioById(id: Int) = audioDao.deleteAudio(id)

    suspend fun deleteAllAudioData() = audioDao.deleteAllAudio()

    suspend fun insertAudio(audio: AudioDes) = audioDao.insertAudio(audio)

}