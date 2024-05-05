package com.illidancstormrage.cstormmemo.ui.record.exception

//非法订单号业务异常
class OrderInvalidException : Exception {
    constructor() : super()
    constructor(msg: String) : super(msg)
}