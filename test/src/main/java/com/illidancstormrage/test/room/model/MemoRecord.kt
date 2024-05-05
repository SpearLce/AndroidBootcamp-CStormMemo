package com.illidancstormrage.test.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class MemoRecord(
    var title: String,
    var text: String,
    var lastEditTimeStamp: Long,
    @ColumnInfo("category_id") var categoryId: Long?,//一对多:一
    @ColumnInfo("audio_id") var audioId: Long?,//一对多:一 (?可以设置为NULL)
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 //id放最后(易于编程)，自动id情况下，默认参数仅提供占位
)
//需要并表查询/group