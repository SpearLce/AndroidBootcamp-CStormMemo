package com.illidancstormrage.cstormmemo.utils.network
// json convert = kotlinx-serialization  三者缺一不可
import com.illidancstormrage.cstormmemo.utils.network.interceptor.XunFeiInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
// retrofit
import retrofit2.Retrofit


object RetrofitCreator {

    /*
    1、文件上传：http[s]: //raasr.xfyun.cn/v2/api/upload
    2、获取结果：http[s]: //raasr.xfyun.cn/v2/api/getResult
     */

    private const val BASE_URL = "https://raasr.xfyun.cn/v2/api/"

    //配置JsonBuilder
    val json = Json {
        prettyPrint = true //格式化输出
        encodeDefaults = true //默认值参数序列化
        ignoreUnknownKeys = true //忽略未知键
        //classDiscriminator = "status" //类鉴别属性 区分 status = ok / failed
    }



    //okhttp3 拦截器
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(XunFeiInterceptor())
        .build()



    //构建retrofit
    // 使用json convert = kotlinx-serialization
    // Json  asConverterFactory  toMediaType 三者依赖
    // kotlinx.serialization Json
    // retrofit2.converter.kotlinx.serialization.asConverterFactory 扩展函数
    // okhttp3
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    //重写create  <T>
    //两种写法，第二种泛型实化
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)

}