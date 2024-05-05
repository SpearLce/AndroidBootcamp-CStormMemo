package com.illidancstormrage.utils.initializer

import android.content.Context
import com.illidancstormrage.utils.log.LogUtil


/**
 * 工具库utils模块 单例入口
 */
object CStormrageToolkitInitializer : UtilsInitializer {
    /**
     * 不要使用 applicationContext 命名，以免在扩展函数中误用未初始化的局部变量。
     * * 解决方案：internal 限制模块内
     */
    internal var appContext: Context? = null
    var logLevel: Int = LogUtil.DEBUG

    override fun initializeContext(context: Context?) {
        if (context != null) {
            appContext = context
        }
    }

    override fun initializeLogLevel(logLevel: Int) {
        this.logLevel = logLevel
    }

    /**
     * 简化初始化高阶函数
     * @param block 调用初始化函数
     */
    inline fun initialize(block: CStormrageToolkitInitializer.() -> Unit) {
        CStormrageToolkitInitializer.block()
    }

}