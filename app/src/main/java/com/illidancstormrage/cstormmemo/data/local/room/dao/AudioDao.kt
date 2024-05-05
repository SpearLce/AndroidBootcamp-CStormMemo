package com.illidancstormrage.cstormmemo.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.illidancstormrage.cstormmemo.model.audio.Audio
import com.illidancstormrage.cstormmemo.model.audio.AudioWithMemos
import com.illidancstormrage.utils.database.room.dao.BaseDao

@Dao
abstract class AudioDao : BaseDao<Audio>() {
    @Transaction
    @Query("select * from Audio where id = :id")
    abstract fun selectAudioWithMemos(id: Long): AudioWithMemos
}