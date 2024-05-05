package com.illidancstormrage.cstormmemo.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.illidancstormrage.cstormmemo.model.history.History
import com.illidancstormrage.utils.database.room.dao.BaseDao

@Dao
abstract class HistoryDao : BaseDao<History>() {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertIgnore(history: History): Long
}

