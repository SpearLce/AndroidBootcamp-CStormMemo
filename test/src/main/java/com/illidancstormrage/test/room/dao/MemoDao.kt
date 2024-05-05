package com.illidancstormrage.test.room.dao

import androidx.room.Dao
import androidx.room.Insert
import com.illidancstormrage.test.room.model.MemoRecord

@Dao
interface MemoDao {
    @Insert
    fun insert(memoRecord: MemoRecord): Long
}