package com.example.vext.utils

import android.content.ContentValues
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.vext.data.local.model.Audio
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class TrashBin(
    context: Context
) {
    private val trashDir = File(context.filesDir,"trashBin")
    private val scheduler = Executors.newScheduledThreadPool(1)
    private val daysDelete = 30L * 24L * 60L * 60L
    private var trashList: List<Audio> = emptyList()
    init {
        if (!trashDir.exists()) {
            trashDir.mkdir()
        }
    }

    fun getTrashList() {
        val retriever = MediaMetadataRetriever()
        trashDir.listFiles()?.forEach { file ->
            retriever.setDataSource(file.absolutePath)
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toIntOrNull() ?: 0
            val displayName = file.name
            val id = file.hashCode().toLong() // Use hashCode as a placeholder for ID
            val data = file.absolutePath // Use file path as data

            val audio = Audio(
                uri = Uri.fromFile(file),
                displayName = displayName,
                id = id, // Use hashCode as a placeholder for ID
                artist = artist ?: "Unknown", // Use metadata or "Unknown" if not available
                data = data, // Use file path as data
                duration = duration, // Use metadata or 0 if not available
                title = title ?: file.nameWithoutExtension // Use metadata or file name if not available
            )
            trashList += audio
        }
        retriever.release()
    }

    fun moveToTrash(context: Context, audio: Audio) {
        val resolver = context.contentResolver
        val sourceUri = audio.uri
        val destinationFile = File(trashDir, audio.displayName)

        resolver.openInputStream(sourceUri)?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Delete the original file
        resolver.delete(sourceUri, null, null)

        scheduler.schedule({ destinationFile.delete() }, daysDelete, TimeUnit.SECONDS)
    }

    fun moveToMediaStore(context: Context, audioFile: File) {
        val resolver = context.contentResolver
        val sourceUri = Uri.fromFile(audioFile)
        val displayName = audioFile.name
        val mimeType = "audio/mp3" // Replace with the actual MIME type if available

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        }

        val destinationUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

        destinationUri?.let {
            resolver.openOutputStream(sourceUri)?.use { outputStream ->
                audioFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Delete the original file
            audioFile.delete()
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun scheduleCleanup() {
//        val scheduler = Executors.newScheduledThreadPool(1)
//        val daily = 24L * 60L * 60L
//        scheduler.scheduleWithFixedDelay(::cleanup, daily, daily, TimeUnit.SECONDS)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun cleanup() {
//        val thirtyDaysAgo = Instant.now().minus(Duration.ofDays(30))
//        trashDir.listFiles()?.forEach { file ->
//            val creationTime = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java).creationTime().toInstant()
//            if (creationTime.isBefore(thirtyDaysAgo)) {
//                file.delete()
//            }
//        }
//    }
}