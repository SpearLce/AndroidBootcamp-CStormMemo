package com.illidancstormrage.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import cn.hutool.core.date.DateUtil
import com.illidancstormrage.test.room.dao.User
import com.illidancstormrage.test.room.database.CSMemoDatabase
import com.illidancstormrage.test.room.model.History
import com.illidancstormrage.test.room.provider.data.DataUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Random
import kotlin.concurrent.thread
import kotlin.random.asKotlinRandom

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = CSMemoDatabase.getMemoryDatabase(this@MainActivity)
        lifecycleScope.launch(Dispatchers.IO) {

            val testDao = db.testDao()
            val memoDao = db.memoDao()
            val historyDao = db.historyDao()

            //准备数据(静态数据)
            for (memo in DataUtil.memoRecordList) {
                memoDao.insert(memo)
            }
            for (history in DataUtil.historyList) {
                historyDao.insert(history)
            }

            //测试插入多条history(关联外键)
            //静态 2 条
            val history = History(
                memoId = 4,
                historyContent = "123",
                editTime = 123144,
                id = 0
            )
            historyDao.insert(history)
            historyDao.insert(history)
            //动态

            findViewById<Button>(R.id.insert).setOnClickListener {
                thread {
                    val memoId = (Random().asKotlinRandom().nextInt(4) + 1).toLong()
                    val history2 = History(
                        memoId = if (memoId > 0L) memoId else 1L,
                        historyContent = "123",
                        editTime = DateUtil.date().time,
                        id = 0
                    )
                    historyDao.insert(history2)
                }
            }
        }

    }
}
//tag=:EditorFragment tag=:CSToolImage tag=:androidruntime  tag=:EditorViewModel tag=:test tag=:testInsert