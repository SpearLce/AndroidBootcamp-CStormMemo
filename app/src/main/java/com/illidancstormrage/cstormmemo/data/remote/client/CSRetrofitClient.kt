package com.illidancstormrage.cstormmemo.data.remote.client

import com.illidancstormrage.cstormmemo.data.remote.service.AudioToTextService
import com.illidancstormrage.cstormmemo.utils.network.RetrofitCreator
import okhttp3.RequestBody
import retrofit2.await

object CSRetrofitClient {
    //引入符合service代理对象
    private val audioToTextService = RetrofitCreator.create<AudioToTextService>()

    //retrofit支持协程
    //三种改法
    //1  将service中方法改suspend函数，去掉Call，该函数直接在协程或挂起函数中 :T
    //2  使用Flow - 协程一部分 (retrofit支持flow)   :Flow<T>
    //3  在client封装类中，定义挂起函数 + await + suspendCoroutine
    suspend fun uploadAudioFile(
        fileName: String,
        fileSize: Long,
        duration: Long,
        timeStamp: String,
        signature: String,
        //audioFile: MultipartBody.Part
        audioFile: RequestBody
    ) = audioToTextService.uploadAudioFile(
        fileName,
        fileSize,
        duration,
        timeStamp,
        signature,
        audioFile
    ).await()

    suspend fun getAudioToTextResult(
        timeStamp: String,
        signature: String,
        orderId: String
    ) = audioToTextService.getAudioToTextResult(timeStamp, signature, orderId).await()


}