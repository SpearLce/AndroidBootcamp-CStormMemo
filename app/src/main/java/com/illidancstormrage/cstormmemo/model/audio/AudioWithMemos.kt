package com.illidancstormrage.cstormmemo.model.audio

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord


data class AudioWithMemos(
    @Embedded var audio: Audio,
    @Relation(
        entity = MemoRecord::class,
        parentColumn = "id",
        entityColumn = "audio_id"
    )
    val memos : List<MemoRecord>
)
