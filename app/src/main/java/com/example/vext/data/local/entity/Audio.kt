package com.example.vext.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vext.model.Audio

@Entity(tableName = "audio")
data class AudioDes(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "audio_name") var audioName: String,
    @ColumnInfo(name = "audio_duration") var audioDuration: Long,
    @ColumnInfo(name = "audio_path") var audioPath: String,
    @ColumnInfo(name = "audio_created") var audioCreated: Long,
    @ColumnInfo(name = "audio_added") var audioAdded: Long,
    @ColumnInfo(name = "audio_removed") var audioRemoved: Long,
    @ColumnInfo(name = "audio_size") var audioSize: Long,
    @ColumnInfo(name = "audio_type") var audioType: String,
    @ColumnInfo(name = "audio_channel") var audioChannel: Int,
    @ColumnInfo(name = "audio_bitrate") var audioBitrate: Int,
    @ColumnInfo(name = "audio_sample_rate") var audioSampleRate: Int,
    @ColumnInfo(name = "audio_waveform_processed") var audioWaveformProcessed: Boolean,
    @ColumnInfo(name = "audio_bookmarked") var audioFavorite: Boolean,
)

fun AudioDes.toAudio(): Audio {
    return Audio(
        id = this.id.toLong(),
        uri = Uri.parse(this.audioPath),
        displayName = this.audioName,
        artist = "", // provide a way to get the artist
        data = this.audioPath,
        duration = this.audioDuration.toInt(),
        title = this.audioName, // provide a way to get the title
        audioCreated = this.audioCreated,
        audioSize = this.audioSize,
        audioFavorite = this.audioFavorite
    )
}