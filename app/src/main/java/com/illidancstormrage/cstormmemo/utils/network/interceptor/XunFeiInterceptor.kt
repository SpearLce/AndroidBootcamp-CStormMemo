package com.illidancstormrage.cstormmemo.utils.network.interceptor

import com.illidancstormrage.utils.log.LogUtil
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Response
import java.io.IOException

class XunFeiInterceptor:Interceptor {
    companion object{
        private const val TAG = "RetrofitRequest"
    }
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString() // 获取请求的URL
        val method = request.method // 请求方法，如GET、POST等

        //1 url
        LogUtil.e("RetrofitRequest", "请求方法: $method, URL: $url")


        //2 请求头
        // 打印请求头信息
        val headers = request.headers
        LogUtil.e("RetrofitRequest", "headers: $headers")
        for (name in headers.names()) {
            LogUtil.e("RetrofitRequest", "Header: $name: ${headers[name]}")
        }

        //3 检查请求体是否为文件上传类型
        val requestBody = request.body
        if (requestBody is MultipartBody) {
            // 遍历所有part，查找文件part
            for (part in requestBody.parts) {
                if (part.headers?.get("Content-Disposition")?.contains("filename=") == true) {
                    // 确认文件存在
                    val fileName =
                        part.headers!!["Content-Disposition"]?.substringAfter("filename=")
                            ?.removeSurrounding("\"")
                    LogUtil.e("RetrofitRequest", "上传文件名: $fileName")
                    // 可以进一步检查文件内容，但通常直接检查RequestBody不切实际，因为内容可能很大
                    // 这里仅做存在性确认
                }
            }
        }
        return chain.proceed(request)
    }
}