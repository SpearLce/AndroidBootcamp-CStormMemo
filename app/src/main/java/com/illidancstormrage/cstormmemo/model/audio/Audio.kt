package com.illidancstormrage.cstormmemo.model.audio

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//@Entity
@Entity(
    tableName = "Audio", //必须存在，下面才能使用，否则kapt报错，且无@Unique注解
    indices = [Index(value = ["uri"], unique = true)]
)
data class Audio(
    var uri: String?,
    @ColumnInfo("order_id") var orderId: String, //不是外键，仅字符串
    @ColumnInfo("audio_to_text") var audioToText: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)
