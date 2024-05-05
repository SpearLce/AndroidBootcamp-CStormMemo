package com.illidancstormrage.cstormmemo.model.category

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord


data class CategoryWithMemos(
    @Embedded var category: Category,
    @Relation(
        entity = MemoRecord::class,
        parentColumn = "id",
        entityColumn = "category_id"
    )
    val memos : List<MemoRecord>
)
