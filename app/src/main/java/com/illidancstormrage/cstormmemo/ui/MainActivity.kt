package com.illidancstormrage.cstormmemo.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.hutool.core.date.DateUtil
import com.illidancstormrage.cstormmemo.R
import com.illidancstormrage.cstormmemo.data.local.room.dao.Test
import com.illidancstormrage.cstormmemo.data.local.room.database.CSMemoDatabase
import com.illidancstormrage.cstormmemo.databinding.ActivityMainBinding
import com.illidancstormrage.cstormmemo.model.category.Category
import com.illidancstormrage.cstormmemo.model.history.History
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.cstormmemo.ui.editor.EditorFragment
import com.illidancstormrage.cstormmemo.utils.data.DataUtil
import com.illidancstormrage.utils.database.room.condition.AbstractWrapper
import com.illidancstormrage.utils.database.room.condition.QueryWrapper
import com.illidancstormrage.utils.log.LogUtil
import com.illidancstormrage.utils.toast.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val sharedViewModel by viewModels<SharedViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        /**
         * 导航到edit，隐藏底边栏，防止误触
         * 误触会导致 setupWithNavController自动设置导航逻辑 与 自己跳转使用navigate逻辑冲突，闪退
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_edit) {
                //binding.mainToolbar.visibility = View.GONE
                navView.visibility = View.GONE
            } else {
                //binding.mainToolbar.visibility = View.VISIBLE
                navView.visibility = View.VISIBLE
            }
        }




        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_list,
                //R.id.navigation_edit,//如果注释掉，会显示up向上返回键
                R.id.navigation_search
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration) //设置toolbar与导航栏信息关联

        // 1 setupWithNavController重载将 NavigationView 或 BottomNavigationView 的实例
        // 与 NavController 实例相关联
        // 2 当您使用 onNavDestinationSelected() 创建自定义导航菜单界面，
        // 且该界面与 NavController 实例托管的目的地相关联时。
        // 注意 编程导航 与 手动导航 不要重叠使用
        navView.setupWithNavController(navController) //导航逻辑由NavigationUI实现类托管 - 即底边栏按钮控制导航(手动导航)
        // (自己navigate(编程导航)会冲突，注意重写，或避免重叠使用 - 非重叠使用即进出栈使用同一套导航逻辑)

        //防止多点误触
        //添加的监听器要放到 setupWithNavController 后面，防止被setupWithNavController中监听器覆盖
        navView.setOnItemSelectedListener { item ->
            val currentDestinationId = navController.currentDestination?.id ?: R.id.navigation_list
            when (item.itemId) {
                R.id.navigation_list -> {
                    if (currentDestinationId != R.id.navigation_list) {
                        navController.navigate(R.id.navigation_list)
                    }
                    true
                }

                R.id.navigation_edit -> {
                    if (currentDestinationId != R.id.navigation_edit) {
                        navController.navigate(R.id.navigation_edit)
                    }
                    true
                }

                R.id.navigation_search -> {
                    if (currentDestinationId != R.id.navigation_search) {
                        navController.navigate(R.id.navigation_search)
                    }
                    true
                }

                else -> false
            }

        }


        //数据库
        //-----------------------------------------------------------------------------------
        val database = CSMemoDatabase.getMemoryDatabase(this)

        //测试
        //填充数据到数据库中(内存数据库)
        prepareDataInDb(database) //执行一次 - 准备数据

        //testForeignKeyDelete(database)

        //testWhereCase(database)

        //testInsert()

    }

    private fun testInsert() {
        "testInsert".makeToast()
        val memoRecord = MemoRecord(
            title = "test",
            text = "gaga",
            lastEditTimeStamp = DateUtil.date().time,
            categoryId = null, //从下拉框中获得列表id
            audioId = null, //为0设置null
            //id = 0
        )
        LogUtil.e("testInsert", "memoRecord - $memoRecord")
        LogUtil.e("testInsert", "保存协程前")

        lifecycleScope.launch(Dispatchers.IO) {
            //1 保存memo，获取memoId
            val resId = LocalRepository.saveOneMemoRecord(memoRecord)

            //LogUtil.e("testInsert","resId = $resId")

            val history = History(
                memoId = resId,
                historyContent = memoRecord.text,
                editTime = memoRecord.lastEditTimeStamp,
                //id = 0
            )
            val test = Test(
                memoId = resId,
                historyContent = memoRecord.text,
                editTime = memoRecord.lastEditTimeStamp,
                id = 0
            )
            val resHistoryId = LocalRepository.saveOneHistory(history)
            LocalRepository.testDao.insert(test)
//            LocalRepository.saveOneHistory(history)
//            LocalRepository.saveOneHistory(history)
//            LocalRepository.saveOneHistory(history)
            //2 保存history
            /*if (resId > 0) {
                val history = History(
                    memoId = resId,
                    historyContent = memoRecord.text,
                    editTime = memoRecord.lastEditTimeStamp,
                    id = 0
                )
                val resHistoryId = LocalRepository.saveOneHistory(history)
                if (resHistoryId > 0) {
                    "保存备忘录, 保存历史成功".makeToast()
                }
            } else {
                "保存失败".makeToast()
            }*/
        }
    }


    private fun testWhereCase(database: CSMemoDatabase) {
        val categoryDao = database.categoryDao()
        val audioDao = database.audioDao()
        val memoDao = database.memoDao()
        val historyDao = database.historyDao()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000)
            //test: 条件器
            val conditionWrapper: AbstractWrapper = QueryWrapper()
            conditionWrapper.eq("id", 1)
            LogUtil.e("test", "${memoDao.selectList(conditionWrapper)}")

            //test：MemoWithHistories 查主表(memo)就行 / 写在Dao中
            val memoWithHistories = memoDao.selectMemoWithHistories(1)
            LogUtil.e("test", "memoRecord = ${memoWithHistories.memoRecord}")
            LogUtil.e("test", "histories = ${memoWithHistories.histories}")


        }
    }

    private fun testForeignKeyDelete(database: CSMemoDatabase) {
        val categoryDao = database.categoryDao()
        val audioDao = database.audioDao()
        val memoDao = database.memoDao()
        val historyDao = database.historyDao()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000) //模拟join等待数据准备好
            //删除主，测试外表
            //1 删除 标签 category，memo 记录设置为null(默认非空看行不行/不行/保证字段Int?可空)
            categoryDao.delete(Category("日常饮食", 1))
            memoDao.delete(
                MemoRecord(
                    title = "tempor",
                    text = "salutatus",
                    lastEditTimeStamp = 7521,
                    categoryId = null,
                    audioId = null,
                    //id = 1
                )
            )
        }
    }

    private fun prepareDataInDb(database: CSMemoDatabase) {
        //内存数据库 - 测试
        val categoryDao = database.categoryDao()
        val audioDao = database.audioDao()
        val memoDao = database.memoDao()
        val historyDao = database.historyDao()


        lifecycleScope.launch(Dispatchers.IO) {
            //category
            for (tagName in DataUtil.categoryList) {
                val category = Category(tagName)
                categoryDao.insert(category)
            }
            //audio
            for (audio in DataUtil.audioList) {
                audioDao.insert(audio)
            }
            //外键存在，现有主表主键，再存在外键，所以先生成memo，再生成history
            for (memo in DataUtil.memoRecordList) {
                memoDao.insert(memo)
            }
            for (history in DataUtil.historyList) {
                historyDao.insert(history)
            }

            testInsert()
        }


    }


    /**
     * 为了确保当用户触发向上导航时，能够正确地返回到上一级或者适当的Destination，
     * 同时考虑到了应用内导航结构的配置，并且提供了向父类或默认行为回退的机制
     * @return Boolean
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EditorFragment.FROM_ALBUM -> {
                if (resultCode == Activity.RESULT_OK) {
                    LogUtil.n(EditorFragment.TAG, "onActivityResult: FROM_ALBUM 打开相册")
                    //binding.CSTextEditor.onActivityResult(data)
                    if (data != null) {
                        sharedViewModel.setData(data)
                    }
                }
            }
        }
    }
}