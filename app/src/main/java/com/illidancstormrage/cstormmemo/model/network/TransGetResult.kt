package com.illidancstormrage.cstormmemo.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransGetResult(
    @SerialName("code")
    var code: String, // 000000
    @SerialName("content")
    var content: Content?,
    @SerialName("descInfo")
    var descInfo: String // success
) {
    @Serializable
    data class Content(
        @SerialName("orderInfo")
        var orderInfo: OrderInfo,
        @SerialName("orderResult")
        var orderResult: String, // {"lattice":[{"json_1best":"{\"st\":{\"sc\":\"0.00\",\"pa\":
        @SerialName("taskEstimateTime")
        var taskEstimateTime: Long // 0 - 任务预估时间
    ) {
        @Serializable
        data class OrderInfo(
            @SerialName("expireTime")
            var expireTime: Long, // 1714610030000 到期时间 !
            @SerialName("failType")
            var failType: Int, // 0(音频正常执行) - 订单异常状态 !
            @SerialName("orderId")
            var orderId: String, // DKHJQ20240429083542638G2qRqGehHchFT3ek
            @SerialName("originalDuration")
            var originalDuration: Long, // 221548
            @SerialName("realDuration")
            var realDuration: Long, // 221548
            @SerialName("status")
            var status: Int // 4(订单已完成) - 订单流程状态 !
        )
    }
}