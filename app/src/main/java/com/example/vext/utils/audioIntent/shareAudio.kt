package com.example.vext.utils.audioIntent

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.vext.model.Audio
import com.example.vext.utils.getRealPathFromURI
import java.io.File

fun shareAudio(context: Context, audio: List<Audio>){
    val intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        type = "audio/*"
        putParcelableArrayListExtra(
            Intent.EXTRA_STREAM,
            ArrayList(audio.map { audio ->
                val audioFile = File(getRealPathFromURI(context, audio.uri))
                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    audioFile
                )
            })
        )
    }
    val chooserIntent = Intent.createChooser(intent, "Share Audio")
    context.startActivity(chooserIntent)
}