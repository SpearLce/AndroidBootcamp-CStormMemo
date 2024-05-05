package com.illidancstormrage.csrich.utils

import com.illidancstormrage.utils.log.LogUtil

object DebugUtil {
    var DEBUG = true
    fun isDebug(): Boolean {
        LogUtil.e("DebugUtil","启用 Debug")
        return true.also { _ -> DEBUG = true }
    }
    fun isNotDebug(): Boolean {
        LogUtil.e("DebugUtil","停用 Debug")
        return false.also { _ -> DEBUG = false }
    }
}