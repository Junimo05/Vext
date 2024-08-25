package com.example.vext.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    try {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return cursor?.getString(columnIndex ?: 0) ?: ""
    } finally {
        cursor?.close()
    }
}

//fun checkAudioNameExistsInMediaStore(context: Context, filename: String): Boolean {
//    val projection = arrayOf(MediaStore.Audio.Media.DISPLAY_NAME)
//    val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} = ?"
//    val selectionArgs = arrayOf(filename)
//    val cursor = context.contentResolver.query(
//        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//        projection,
//        selection,
//        selectionArgs,
//        null
//    )
//    Log.e("checkAudioNameExistsInMediaStore", cursor?.moveToFirst().toString())
//    val exists = cursor?.moveToFirst() == true
//    cursor?.close()
//    Log.e("checkAudioNameExistsInMediaStore", "exists: $exists")
//    return exists
//}

fun checkAudioExistsInMediaStore(context: Context, audioPath: Uri?): Boolean {
    if (audioPath == null) {

        return false
    }
//    Log.e("checkAudioExistsInMediaStore", "audioPath: $audioPath")
    val projection = arrayOf(MediaStore.Audio.Media._ID)
    val selection = "${MediaStore.Audio.Media._ID} = ?"
    val selectionArgs = arrayOf(audioPath.lastPathSegment)
    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )

    val exists = cursor?.moveToFirst() ?: false
//    Log.e("checkAudioExistsInMediaStore", "exists: $exists")
    cursor?.close()
    return exists
}

fun renameFile(context: Context, uri: Uri, newName: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, newName)
    }

    context.contentResolver.update(uri, contentValues, null, null)
}
