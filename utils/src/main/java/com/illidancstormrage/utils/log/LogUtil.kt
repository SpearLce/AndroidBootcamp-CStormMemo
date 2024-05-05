package com.illidancstormrage.utils.log

import android.util.Log
import com.illidancstormrage.utils.initializer.CStormrageToolkitInitializer

object LogUtil {
    const val VERBOSE = 1 //<=1 等级低，输出多
    const val DEBUG = 2
    const val INFO = 3
    const val WARN = 4
    const val ERROR = 5 //等级高，输出少
    const val NOLOG = 6 //>=6

    //这里通过调类的等级，如果是6，那么都不会所有log都不会输出
    var logLevel = CStormrageToolkitInitializer.logLevel

    @JvmStatic
    fun v(tag: String = "log-verbose", msg: String) {
        if (logLevel <= VERBOSE) {
            Log.v(tag, msg)
        }
    }

    @JvmStatic
    fun d(tag: String = "log-debug", msg: String) {
        if (logLevel <= DEBUG) {
            Log.d(tag, msg)
        }
    }

    @JvmStatic
    fun i(tag: String = "log-info", msg: String) {
        if (logLevel <= INFO) {
            Log.i(tag, msg)
        }
    }

    @JvmStatic
    fun w(tag: String = "log-warn", msg: String) {
        if (logLevel <= WARN) {
            Log.w(tag, msg)
        }
    }

    @JvmStatic
    fun e(tag: String = "log-error", msg: String) {
        if (logLevel <= ERROR) {
            Log.e(tag, msg)
        }
    }

    @JvmStatic
    fun n(tag: String = "no-log", msg: String) {
        //if (logLevel >= NOLOG)
    }
}