package com.example.vext.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vext.data.local.entity.AudioDes

@Dao
interface AudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudio(audio: AudioDes)

    //Get
    @Query("SELECT * FROM audio")
    suspend fun getAllAudio(): List<AudioDes>

    @Query("SELECT * FROM audio WHERE id = :id")
    suspend fun getAudio(id: Int): AudioDes

    @Query("SELECT * FROM audio WHERE audio_removed != 0")
    suspend fun getRemovedAudio(): List<AudioDes>


    //Delete
    @Query("DELETE FROM audio WHERE id = :id")
    suspend fun deleteAudio(id: String)

    @Query("DELETE FROM audio")
    suspend fun deleteAllAudio()

    @Query("DELETE FROM audio WHERE audio_removed != 0")
    suspend fun deleteAllRemovedAudio()
}