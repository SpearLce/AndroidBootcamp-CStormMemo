package com.illidancstormrage.test.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.illidancstormrage.test.room.dao.HistoryDao
import com.illidancstormrage.test.room.dao.MemoDao
import com.illidancstormrage.test.room.dao.TestDao
import com.illidancstormrage.test.room.dao.User
import com.illidancstormrage.test.room.model.History
import com.illidancstormrage.test.room.model.MemoRecord

@Database(
    entities = [
        User::class, History::class, MemoRecord::class
    ], version = 1, exportSchema = false
)
abstract class CSMemoDatabase : RoomDatabase() {

    abstract fun testDao(): TestDao
    abstract fun memoDao(): MemoDao
    abstract fun historyDao(): HistoryDao

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