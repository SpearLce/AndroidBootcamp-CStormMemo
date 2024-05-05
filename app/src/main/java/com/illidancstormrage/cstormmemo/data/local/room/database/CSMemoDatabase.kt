package com.illidancstormrage.cstormmemo.data.local.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.illidancstormrage.cstormmemo.data.local.room.dao.AudioDao
import com.illidancstormrage.cstormmemo.data.local.room.dao.CategoryDao
import com.illidancstormrage.cstormmemo.data.local.room.dao.HisDao
import com.illidancstormrage.cstormmemo.data.local.room.dao.HistoryDao
import com.illidancstormrage.cstormmemo.data.local.room.dao.MemoDao
import com.illidancstormrage.cstormmemo.data.local.room.dao.Test
import com.illidancstormrage.cstormmemo.data.local.room.dao.TestDao
import com.illidancstormrage.cstormmemo.model.audio.Audio
import com.illidancstormrage.cstormmemo.model.category.Category
import com.illidancstormrage.cstormmemo.model.history.History
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord

@Database(
    entities = [
        Audio::class, Category::class, History::class, MemoRecord::class, Test::class
    ], version = 1, exportSchema = false
)
abstract class CSMemoDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
    abstract fun categoryDao(): CategoryDao
    abstract fun historyDao(): HistoryDao
    abstract fun memoDao(): MemoDao
    abstract fun testDao(): TestDao
    abstract fun hisDao():HisDao

    companion object {
        //db单例模式
        //1 存放单例的变量
        private var instance: CSMemoDatabase? = null
        private var instanceInMemory: CSMemoDatabase? = null

        @Synchronized //保证线程安全，防止多线程环境下初始化多个数据库实例
        fun getLocalDatabase(context: Context): CSMemoDatabase { //一个静态工厂方法
            //2 判断单例变量是否为空
            instance?.let {
                //不为空，单例返回
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,//app上下文
                CSMemoDatabase::class.java, //db.class
                "app_database"//数据库名
            ).build().apply {
                instance = this //保存单例
            }
        }

        @Synchronized
        fun getMemoryDatabase(context: Context) = if (instanceInMemory != null) {
            instanceInMemory!!
        } else {
            Room.inMemoryDatabaseBuilder(
                context,
                CSMemoDatabase::class.java
            ).build().apply {
                instanceInMemory = this
            }
        }

    }
}