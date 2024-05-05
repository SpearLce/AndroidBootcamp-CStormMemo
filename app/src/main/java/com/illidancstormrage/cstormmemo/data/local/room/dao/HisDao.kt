package com.illidancstormrage.cstormmemo.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.illidancstormrage.cstormmemo.model.history.History


@Dao
interface HisDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertIgnore(history: History): Long
}