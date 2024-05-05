package com.illidancstormrage.cstormmemo.model.memo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.illidancstormrage.cstormmemo.model.audio.Audio
import com.illidancstormrage.cstormmemo.model.category.Category
import org.jetbrains.annotations.Nullable

@Entity(
    tableName = "MemoRecord",
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["audio_id"]),
        //Index(value = ["lastEditTimeStamp"], orders = [Index.Order.DESC]) //暂时无法工作
    ],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL //没有标签,set null
        ),
        ForeignKey(
            entity = Audio::class,
            parentColumns = ["id"],
            childColumns = ["audio_id"],
            onDelete = ForeignKey.SET_NULL //没有音频,set null
        )
    ]
)
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