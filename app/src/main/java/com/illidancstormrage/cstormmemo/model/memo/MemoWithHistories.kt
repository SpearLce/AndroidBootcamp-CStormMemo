package com.illidancstormrage.cstormmemo.model.memo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.illidancstormrage.cstormmemo.model.history.History


data class MemoWithHistories(
    //一对多
    //一：
    @Embedded var memoRecord: MemoRecord,
    @Relation(
        entity = History::class,
        parentColumn = "id",//父表id(一)
        entityColumn = "memo_id"
    )
    var histories: List<History>
)