package com.illidancstormrage.cstormmemo.repository

import com.illidancstormrage.cstormmemo.data.remote.client.CSRetrofitClient
import com.illidancstormrage.cstormmemo.data.remote.config.ApiClientConfig
import com.illidancstormrage.cstormmemo.model.network.TransGetResult
import com.illidancstormrage.cstormmemo.model.network.TransUploadResult
import com.illidancstormrage.cstormmemo.model.network.result.ResultCode
import com.illidancstormrage.cstormmemo.utils.extensions.hmacSHA1
import com.illidancstormrage.cstormmemo.utils.extensions.md5
import com.illidancstormrage.utils.log.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.RequestBody

object RemoteRepository {

    private const val TAG = "RemoteRepository"

    suspend fun uploadAudioFileForResult(
        fileName: String,
        fileSize: Long,
        duration: Long,
        timeStamp: String,
        //audioFile: MultipartBody.Part
        audioFile: RequestBody
    ): Result<TransUploadResult> {
        val coroutineScope = CoroutineScope(Dispatchers.IO).async {

            val signature = buildSignature(timeStamp)//处理

            val updateAudioResult =
                CSRetrofitClient.uploadAudioFile(
                    fileName,
                    fileSize,
                    duration,
                    timeStamp,
                    signature,
                    audioFile
                )

            val result = try {
                if (updateAudioResult.code == ResultCode.SUCCESS.code) {
                    LogUtil.e(TAG, "返回成功结果 - updateAudioResult = $updateAudioResult")
                    Result.success(updateAudioResult)
                } else {
                    LogUtil.e(TAG, "返回码 不是000000 - 不是成功")
                    Result.success(updateAudioResult)
                    //Result.failure(RuntimeException("response status is ${updateAudioResult.descInfo}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            result
        }
        return coroutineScope.await()
    }

    private fun buildSignature(timeStamp: String): String {
        //1 获取baseString
        //  baseString 由 appId 和当前时间戳 ts 拼接而成；
        //  假如 APPID = 595f23df，ts = 1512041814，则 baseString = 595f23df1512041814
        LogUtil.e(TAG, "Signature: timeStamp = $timeStamp")
        val baseString = ApiClientConfig.APPID + timeStamp
        LogUtil.e(TAG, "Signature: baseString = $baseString")
        // MD5
        val result = baseString.md5().hmacSHA1(ApiClientConfig.SecretKey)
        LogUtil.e(TAG, "Signature: baseString.md5.hmacSHA1 = $result")
        return result
    }

    suspend fun getAudioToTextResult(
        timeStamp: String,
        orderId: String
    ): Result<TransGetResult> {
        val signature = buildSignature(timeStamp)//处理
        val audioToTextResult =
            CSRetrofitClient.getAudioToTextResult(timeStamp, signature, orderId)
        val result = try {
            Result.success(audioToTextResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
        return result
    }

}