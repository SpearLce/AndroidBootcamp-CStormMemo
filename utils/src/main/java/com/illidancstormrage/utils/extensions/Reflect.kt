package com.illidancstormrage.utils.extensions

import kotlin.reflect.KTypeProjection


/**
 * * KTypeProjection.toString 获取直接类名
 * * com.example.User -> User
 * * User -> User
 */
val KTypeProjection.simpleName: String
    get() {
        val name = this.toString()
        return name.substring(name.lastIndexOf(".") + 1)
    }
