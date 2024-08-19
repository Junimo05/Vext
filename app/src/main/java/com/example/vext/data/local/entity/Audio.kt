package com.example.vext.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vext.model.Audio
import java.sql.Blob

@Entity(tableName = "audio")
data class AudioDes(
    @PrimaryKey var id: String,
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
    @ColumnInfo(name = "audio_bookmarked") var audioBookmarked: Boolean,
)

fun AudioDes.toAudio(): Audio {
    return Audio(
        uri = Uri.parse(this.audioPath),
        displayName = this.audioName,
        id = this.id.toLong(),
        artist = "", // You need to provide a way to get the artist
        data = this.audioPath,
        duration = this.audioDuration.toInt(),
        title = this.audioName, // You need to provide a way to get the title
        audioCreated = this.audioCreated,
        audioSize = this.audioSize,
        audioBookmarked = this.audioBookmarked
    )
}