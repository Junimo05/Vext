package com.example.vext.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    try {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return cursor?.getString(column_index ?: 0) ?: ""
    } finally {
        cursor?.close()
    }
}