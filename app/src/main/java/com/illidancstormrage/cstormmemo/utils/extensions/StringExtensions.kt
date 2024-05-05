package com.illidancstormrage.cstormmemo.utils.extensions

import android.net.Uri
import java.io.File

//uri

/**
 * 以content://开头URI 转Uri
 * @receiver String
 * @return Uri?
 */
fun String.parseToUri(): Uri? {
    return if (this.contains("^content://".toRegex())) {
        Uri.parse(this)
    } else {
        null
    }
}

fun String.filePathToUri(): Uri {
    val file = File(this)
    return Uri.fromFile(file)
}

//comment

/**
 * 作为占位符，没有实际功能
 * @receiver String
 * @return String
 */
fun String.pass(): String {
    return this
}