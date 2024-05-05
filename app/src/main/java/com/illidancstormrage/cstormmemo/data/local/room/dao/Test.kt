package com.illidancstormrage.cstormmemo.data.local.room.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Test(
    @ColumnInfo("memo_id") var memoId: Long, //一作为外键
    @ColumnInfo("history_content") var historyContent: String, //history_content
    @ColumnInfo("edit_time") var editTime: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 //id放最后(易于编程)，自动id情况下，默认参数仅提供占位
)

