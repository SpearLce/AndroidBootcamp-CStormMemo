package com.illidancstormrage.cstormmemo.utils.calendar

import android.icu.util.Calendar

fun Calendar.dayStart(): Long {
    // 设置时间为当天的0点0分0秒
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return timeInMillis
}

fun Calendar.dayEnd(): Long {
    // 设置时间为当天的23点59分59秒
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
    return timeInMillis
}