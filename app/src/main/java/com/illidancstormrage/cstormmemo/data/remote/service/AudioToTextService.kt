package com.illidancstormrage.cstormmemo.data.remote.service

import com.illidancstormrage.cstormmemo.data.remote.config.ApiClientConfig
import com.illidancstormrage.cstormmemo.model.network.TransGetResult
import com.illidancstormrage.cstormmemo.model.network.TransUploadResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AudioToTextService {
    /**
     * fileName:
     *
     * fileSize:
     *
     * duration:
     *
     * https://raasr.xfyun.cn/v2/api/upload?
     * duration=200
     * & signa=Je5YsBvPcsbB4qy8Qvzd367fiv0%3D
     * & fileName=%E9%98%B3%E5%85%89%E6%80%BB%E5%9C%A8%E9%A3%8E%E9%9B%A8%E5%90%8E.speex-wb
     * & fileSize=11895
     * & sysDicts=uncivilizedLanguage  无
     * & appId=3e79d91c
     * & ts=1662101767
     * @return Call<TransUploadResult>
     */
    //BASE_URL = "https://raasr.xfyun.cn/v2/api/"
    //请求头 Content-Type: application/json; charset=UTF-8,   Chunked: false
    //      header（Content-Type 为 application/octet-stream
    //@Multipart //Body以Part部分上传
    @POST("upload?appId=${ApiClientConfig.APPID}")
    @Headers("Content-Type: application/octet-stream")
    fun uploadAudioFile(
        @Query("fileName") fileName: String,
        @Query("fileSize") fileSize: Long,
        @Query("duration") duration: Long,
        @Query("ts") timeStamp: String,
        @Query("signa") signature: String,
        //@Part audioFile: MultipartBody.Part
        @Body audioFile: RequestBody
    ): Call<TransUploadResult>


    @POST("getResult?appId=${ApiClientConfig.APPID}")
    fun getAudioToTextResult(
        @Query("ts") timeStamp: String,
        @Query("signa") signature: String,
        @Query("orderId") orderId: String
    ):Call<TransGetResult>

}

/*
https://raasr.xfyun.cn/v2/api/
upload?appId=3b1fe353&fileName=%E5%A4%A9%E4%B8%8B.mp3&fileSize=9045481&duration=221548&ts=1714285848598&signa=jx5kMI8KMgAXb0myLcci52MQ6QM%3D
 */

/*
https://raasr.xfyun.cn/v2/api/getResult?

signa=Wv23VLOg%2F6sQ1BDx4DKnnxtgiwQ%3D 1
&
orderId=DKHJQ2022090217220902175209AAEBD000015 2 订单号必须得
&
appId=3e79d91c 3
&
resultType=predict 4
&
ts=1662112340 5
------------------------------------------------------


Content-Type - 指示资源的MIME类型
Content-Type: multipart/form-data; - 常用于表单提交（Part:键值对(值是文件)）
Content-Type: application/json; charset=UTF-8 - 发送的数据格式是JSON
Content-Type: application/octet-stream - 传输的是二进制数据流(整个body都是二进制流)


 */