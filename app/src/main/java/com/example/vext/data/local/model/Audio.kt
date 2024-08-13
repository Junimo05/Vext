package com.example.vext.data.local.model

import android.net.Uri
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import java.net.URI

@Parcelize
data class Audio(
    val uri: Uri,
    val displayName: String,
    val id: Long,
    val artist: String,
    val data: String,
    val duration: Int,
    val title: String
): Parcelable {
    companion object {
        val DiffCallback = object: DiffUtil.ItemCallback<Audio>() {
            override fun areItemsTheSame(oldItem: Audio, newItem: Audio): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Audio, newItem: Audio): Boolean {
                return oldItem == newItem
            }
        }
    }
}