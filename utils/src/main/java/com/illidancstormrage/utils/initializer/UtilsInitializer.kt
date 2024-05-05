package com.illidancstormrage.utils.initializer

import android.content.Context

interface UtilsInitializer {

    /**
     * 初始化Context函数
     * * 请确保初始化工具库时传入 ApplicationContext。
     */
    fun initializeContext(context: Context?)

    /**
     * 初始化 LogUtil日志工具 - 日志等级
     */
    fun initializeLogLevel(logLevel: Int)


}