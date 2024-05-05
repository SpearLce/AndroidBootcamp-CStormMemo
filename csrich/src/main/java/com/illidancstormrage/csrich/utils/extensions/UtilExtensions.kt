package com.illidancstormrage.csrich.utils.extensions

import android.content.Context
import android.graphics.Point

//screen ----------------------------------------------------
fun Context.getScreenSizeTotalApi30Plus(): Point {
    val realSize = Point()
    //应用程序显示区域指定可能包含应用程序窗口的显示部分，不包括系统装饰
    val displayMetrics = resources.displayMetrics
    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels
    realSize.x = width
    realSize.y = height
    return realSize
}


