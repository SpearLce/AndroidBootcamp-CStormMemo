package com.illidancstormrage.cstormmemo.utils.debug

fun DebugUtil.tag(): Int {
    this.count += 1
    return this.count
}