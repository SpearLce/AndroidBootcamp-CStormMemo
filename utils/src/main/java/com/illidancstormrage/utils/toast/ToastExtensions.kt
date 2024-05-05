package com.illidancstormrage.utils.toast

import android.util.Log
import android.widget.Toast
import com.illidancstormrage.utils.initializer.CStormrageToolkitInitializer
import com.illidancstormrage.utils.log.LogUtil

/**
 * @param duration Toast显示时间
 * @exception NullPointerException 检查工具库初始化 确保applicationContext非空
 */
fun String.makeToast(duration: Int = Toast.LENGTH_SHORT) {
    /**
     * Toast与当前组件生命周期并不紧密
     * Toast参数context，可以提供ApplicationContext
     * 封装库无法提供库使用者的ApplicationContext
     * 所以使用接口：initializer.LibraryInitializer 初始化
     * 在库使用者application自定义派生类中初始化
     */

    try {
        Toast.makeText(
            /**
             * 请务必参照相关文档指示，在使用本扩展函数之前，确保已将应用程序的 ApplicationContext
             * 正确传入并完成初始化过程，以免造成因上下文缺失而导致的功能受限或运行时异常。
             */
            CStormrageToolkitInitializer.appContext,
            this,
            duration
        ).show()
    } catch (e: NullPointerException) {
        LogUtil.e(tag = "error", msg = "检查工具库初始化，确保applicationContext非空")
    }

}



