package com.illidancstormrage.cstormmemo.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransUploadResult(
    @SerialName("code")
    var code: String, // 000000
    @SerialName("descInfo")
    var descInfo: String, // success
    @SerialName("content")
    var content: Content? = null //可空
) {
    @Serializable
    data class Content(
        @SerialName("orderId")
        var orderId: String, // DKHJQ202209021522090215490FAAE7DD0008C
        @SerialName("taskEstimateTime")
        var taskEstimateTime: Long // 28000
    )
}