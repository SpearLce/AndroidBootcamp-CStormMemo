package com.illidancstormrage.cstormmemo.utils.extensions

import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalStdlibApi::class)
fun String.md5(): String {
    //获取 MD5 的 MessageDigest 实例
    val md = MessageDigest.getInstance("MD5")
    //然后计算转换为 ByteArray 的字符串的摘要
    val digest = md.digest(this.toByteArray())
    //成的 MD5 摘要 ByteArray 转换为 String
    return digest.toHexString() //Kotlin 1.9 中引入的实验函数
}

/**
 * 使用 MD5 哈希验证文件校验和
 * @receiver File
 * @return String
 */
@OptIn(ExperimentalStdlibApi::class)
fun File.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.readBytes())
    return digest.toHexString()
}

fun String.hmacSHA1(secretKey: String): String {
    val algorithm = "HmacSHA1"
    val keySpec = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), algorithm)
    val mac = Mac.getInstance(algorithm)
    mac.init(keySpec)
    val rawHmac = mac.doFinal(toByteArray(StandardCharsets.UTF_8))
    return Base64.getEncoder().encodeToString(rawHmac)
}