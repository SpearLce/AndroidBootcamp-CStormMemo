package com.illidancstormrage.cstormmemo.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.model.memo.MemoWithHistories
import com.illidancstormrage.utils.database.room.dao.BaseDao

@Dao
abstract class MemoDao : BaseDao<MemoRecord>() {

    @Transaction
    @Query("select * from MemoRecord where id = :id")
    abstract fun selectMemoWithHistories(id: Long): MemoWithHistories


    @Query("select * from MemoRecord ORDER BY lastEditTimeStamp DESC")
    abstract fun selectAllByDesc(): List<MemoRecord>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertIgnore(memoRecord: MemoRecord): Long
}