package com.illidancstormrage.cstormmemo.model.network.result

enum class ResultCode(val code: String, val msg: String) {
    SUCCESS("000000", "订单返回成功"),
    INVALID_ORDERID("100004", "查询订单错误"),//invalid orderId
    ORDERID_NULL("100039", "订单为空")
}