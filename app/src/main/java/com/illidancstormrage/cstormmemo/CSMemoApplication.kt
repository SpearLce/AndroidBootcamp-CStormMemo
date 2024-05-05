package com.illidancstormrage.cstormmemo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.illidancstormrage.cstormmemo.data.local.room.database.CSMemoDatabase
import com.illidancstormrage.utils.initializer.CStormrageToolkitInitializer
import com.illidancstormrage.utils.log.LogUtil

class CSMemoApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        lateinit var database: CSMemoDatabase
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        database = CSMemoDatabase.getMemoryDatabase(applicationContext)


        CStormrageToolkitInitializer.initialize {
            initializeContext(applicationContext)
            initializeLogLevel(LogUtil.VERBOSE)
        }
    }
}