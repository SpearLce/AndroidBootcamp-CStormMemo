package com.illidancstormrage.cstormmemo.repository

import android.icu.util.Calendar
import com.illidancstormrage.cstormmemo.CSMemoApplication.Companion.database
import com.illidancstormrage.cstormmemo.model.audio.Audio
import com.illidancstormrage.cstormmemo.model.category.Category
import com.illidancstormrage.cstormmemo.model.history.History
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.model.memo.MemoWithHistories
import com.illidancstormrage.cstormmemo.utils.calendar.dayEnd
import com.illidancstormrage.cstormmemo.utils.calendar.dayStart
import com.illidancstormrage.utils.database.room.condition.QueryWrapper

object LocalRepository {
    val categoryDao = database.categoryDao()
    val audioDao = database.audioDao()
    val memoDao = database.memoDao()
    val historyDao = database.historyDao()
    val testDao = database.testDao()
    val hisDao = database.hisDao()

    //memo
    fun getAllMemoList(): List<MemoRecord> {
        return memoDao.selectAll()
    }

    fun getAllMemoListByDesc(): List<MemoRecord> {
        return memoDao.selectAllByDesc()
    }

    fun getOneMemoRecordById(id: Long): MemoRecord {
        return memoDao.selectById(id)
    }

    fun getOneMemoRecordByCategoryId(categoryId: Long): List<MemoRecord> {
        val queryWrapper = QueryWrapper().like("category_id", categoryId)
        return memoDao.selectList(queryWrapper)
    }

    fun saveOneMemoRecord(memoRecord: MemoRecord): Long {
        return memoDao.insertIgnore(memoRecord)
    }

    fun getMemoListByTitleLike(title: String): List<MemoRecord> {
        val queryWrapper = QueryWrapper().like("title", title)
        return memoDao.selectList(queryWrapper)
    }

    fun getMemoListByDate(calendar: Calendar): List<MemoRecord> {
        val dayStart = calendar.dayStart()
        val dayEnd = calendar.dayEnd()
        val queryWrapper = QueryWrapper()
            .lt("lastEditTimeStamp", dayEnd)
            .gt("lastEditTimeStamp", dayStart)
        return memoDao.selectList(queryWrapper)
    }

    fun updateOneMemoRecord(memoRecord: MemoRecord): Int {
        return memoDao.update(memoRecord)
    }

    fun deleteOneMemoRecord(memoRecord: MemoRecord): Int {
        return memoDao.delete(memoRecord)
    }


    //audio
    fun getAllAudioList(): List<Audio> {
        return audioDao.selectAll()
    }


    fun getOneAudioById(id: Long): Audio {
        return audioDao.selectById(id)
    }

    fun getAudioIdByUri(uri: String): Long? {
        val audio = getOneAudioByUri(uri)
        return audio?.id
    }

    fun getOneAudioByUri(uri: String): Audio? {
        val queryWrapper = QueryWrapper().eq("uri", uri)
        return audioDao.selectOne(queryWrapper)
    }

    fun saveOneAudio(audio: Audio): Long {
        return audioDao.insert(audio)
    }

    //category
    fun getAllCategoryList(): List<Category> {
        return categoryDao.selectAll()
    }

    //history
    fun saveOneHistory(history: History): Long {
        return historyDao.insert(history)
    }

    fun deleteOneHistory(history: History): Int {
        return historyDao.delete(history)
    }

    fun getAllHistoryListByDescById(): List<History> {
        //return historyDao.selectAllByDescByMemoId()
        return historyDao.selectAll()
    }

    fun getHistoriesByMemoId(id: Long): MemoWithHistories {
        return memoDao.selectMemoWithHistories(id)
    }

}