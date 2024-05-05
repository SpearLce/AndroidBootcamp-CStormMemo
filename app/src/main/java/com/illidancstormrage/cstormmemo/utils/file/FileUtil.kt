package com.illidancstormrage.cstormmemo.utils.file

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import kotlin.io.path.outputStream

object FileUtil {

    /**
     * 可以判断content://和file://开头uri文件是否存在
     * @param context Context
     * @param uri Uri
     * @return Boolean
     */
    fun isFileExistsAtUri(context: Context, uri: Uri): Boolean {
        return try {
            // 获取文件输入流，如果文件不存在则此操作会失败抛出异常
            context.contentResolver.openInputStream(uri)?.close()
            /*
            不要尝试.DATA
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DATA), null, null, null)
             */
            true
        } catch (e: FileNotFoundException) {
            false
        } catch (e: IOException) {
            false
        }
    }




    /**
     * 支持 content:// 调用contentResolver - DISPLAY_NAME
     *
     * 支持 file://(直接string切片)
     * @param context Context
     * @param uri Uri
     * @return String 返回""(不存在或空名返回"")或者返回文件名
     */
    fun getFileName(context: Context, uri: Uri): String {
        when (uri.scheme) {
            "content" -> {
                var fileName: String? = null
                val contentResolver = context.contentResolver
                fileName = contentResolver.query(uri, null, null, null, null)?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                        if (nameIndex >= 0) {
                            it.getString(nameIndex)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
                if (fileName != null) {
                    return fileName
                }
                return ""
            }

            "file" -> {
                val lastDotPosition = uri.path!!.lastIndexOf('/')
                return if (lastDotPosition == -1 || lastDotPosition == uri.path!!.length - 1) {
                    // 如果没有找到/或者/在字符串末尾，返回空字符串
                    ""
                } else {
                    //audioUri.path = ....../audioRecord_2024_04_29_20_28_17.m4a
                    return uri.path!!.substring(lastDotPosition + 1)//从.开始 (+1名称不需要/)
                }
            }

            else -> {
                return ""
            }
        }

    }

    /**
     * 文件大小 支持content file uri
     * @param context Context
     * @param uri Uri
     * @return Long
     */
    fun getFileSize(context: Context, uri: Uri): Long {
        val contentResolver = context.contentResolver
        var fileSize = 0L
        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r").use {
            fileSize = it?.statSize ?: 0L
        }
        return fileSize


        //第二种写法
        //FileInputStream(audioTempFile).readBytes().size
    }

    /**
     * 音频文件 - 获取音频持续时间 (file://|content://)
     * @param context Context
     * @param uri Uri
     * @return 音频时长,无则返回0L
     */
    fun getDurationMs(context: Context, uri: Uri): Long {
        //获取音频持续时间
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        //val durationTimeView = String.format("%d:%02d:%02d", hours, minutes, seconds)
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull() ?: 0L
    }

    /**
     * 根据contentProvider提供uri创建临时文件 (file://|content://)
     *
     * 使用完记得删除，file.delete() / file.deleteOnExit()等
     * @param context Context
     * @param uri Uri
     * @return File
     */
    fun getTempFile(context: Context, uri: Uri): File {
        var file: File? = null
        val suffix = getFileSuffix(getFileName(context, uri))
        val tempFilePath = kotlin.io.path.createTempFile("temp_audio_", suffix)
        val contentResolver = context.contentResolver

        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        inputStream?.use { input ->
            tempFilePath.outputStream().use { fileOutput ->
                input.copyTo(fileOutput)
            }
        }
        file = tempFilePath.toFile()
        return file
    }

    /**
     * 根据路径获取文件名后缀
     * @param fileName String
     * @return String
     */
    fun getFileSuffix(fileName: String): String {
        val lastDotPosition = fileName.lastIndexOf('.')
        return if (lastDotPosition == -1 || lastDotPosition == fileName.length - 1) {
            // 如果没有找到点或者点在字符串末尾（没有扩展名的情况），返回空字符串
            ""
        } else {
            fileName.substring(lastDotPosition)//从.开始
        }
    }
}