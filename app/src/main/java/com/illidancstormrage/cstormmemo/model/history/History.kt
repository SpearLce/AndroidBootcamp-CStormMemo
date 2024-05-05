package com.illidancstormrage.cstormmemo.model.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord

@Entity(
    //tableName = "History",
    indices = [Index(value = ["memo_id"])],
    foreignKeys = [
        ForeignKey(
            entity = MemoRecord::class,
            parentColumns = ["id"],
            childColumns = ["memo_id"],
            //级联删除
            onDelete = ForeignKey.CASCADE //memo删除了，那么关联历史记录都要删除
            //不用设置更新,id为自增主键
        )
    ]
)
data class History(
    @ColumnInfo("memo_id") var memoId: Long, //一作为外键
    @ColumnInfo("history_content") var historyContent: String, //history_content
    @ColumnInfo("edit_time") var editTime: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 //id放最后(易于编程)，自动id情况下，默认参数仅提供占位
)