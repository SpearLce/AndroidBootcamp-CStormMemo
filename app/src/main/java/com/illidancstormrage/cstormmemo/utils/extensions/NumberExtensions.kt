package com.illidancstormrage.cstormmemo.utils.extensions

import cn.hutool.core.date.DateUtil

fun Int.msToHMSFormatArr(): IntArray {
    val seconds = this / 1000 % 60
    val minutes = this / (1000 * 60) % 60
    val hours = this / (1000 * 60 * 60) % 24
    return intArrayOf(hours, minutes, seconds)
}

fun Long.msToHMSFormatArr(): LongArray {
    val seconds = this / 1000 % 60
    val minutes = this / (1000 * 60) % 60
    val hours = this / (1000 * 60 * 60) % 24
    return longArrayOf(hours, minutes, seconds)
}

fun Long.msFormatDateStr(format: String = "yyyy年MM月dd日 HH时mm分"): String {
    return DateUtil.date(this).toString(format)
}