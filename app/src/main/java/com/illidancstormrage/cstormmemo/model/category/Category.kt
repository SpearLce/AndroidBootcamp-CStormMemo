package com.illidancstormrage.cstormmemo.model.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Category(
    @ColumnInfo("tag_name") var tagName: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 //id放最后(易于编程)，自动id情况下，默认参数仅提供占位
)
