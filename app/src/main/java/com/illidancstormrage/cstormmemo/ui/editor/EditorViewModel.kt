package com.illidancstormrage.cstormmemo.ui.editor

import android.content.Context
import android.icu.util.Calendar
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.hutool.core.date.DateUtil
import com.illidancstormrage.cstormmemo.model.audio.Audio
import com.illidancstormrage.cstormmemo.model.audio.AudioFileInfo
import com.illidancstormrage.cstormmemo.model.category.Category
import com.illidancstormrage.cstormmemo.model.history.History
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.model.network.OrderResult
import com.illidancstormrage.cstormmemo.model.network.result.OrderInfoCode
import com.illidancstormrage.cstormmemo.model.network.result.ResultCode
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.cstormmemo.repository.RemoteRepository
import com.illidancstormrage.cstormmemo.repository.RemoteRepository.getAudioToTextResult
import com.illidancstormrage.cstormmemo.ui.record.exception.OrderInvalidException
import com.illidancstormrage.cstormmemo.utils.debug.DebugUtil
import com.illidancstormrage.cstormmemo.utils.debug.tag
import com.illidancstormrage.cstormmemo.utils.file.FileUtil
import com.illidancstormrage.cstormmemo.utils.file.FileUtil.isFileExistsAtUri
import com.illidancstormrage.cstormmemo.utils.network.RetrofitCreator
import com.illidancstormrage.utils.log.LogUtil
import com.illidancstormrage.utils.toast.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileInputStream
import java.lang.StringBuilder

class EditorViewModel : ViewModel() {

    companion object {
        private const val TAG = "EditorViewModel"
    }

    val memoRecord = MutableLiveData<MemoRecord>()
    val categoryList = MutableLiveData<List<Category>>()

    //写法之一：联动observe时候，不为空写法，id一定初始化为0
    val audio = MutableLiveData<Audio>(Audio(uri = null, orderId = "", audioToText = "", id = 0))
    val isTranslating = MutableLiveData<Boolean>(false) //正在语音转文字 - 刷新圈UI状态
    val audioText = MutableLiveData("")

    //日期选择
    val startCalendar = MutableLiveData<Calendar>()
    val endCalendar = MutableLiveData<Calendar>()

    fun saveMemoToDb(newMemoRecord: MemoRecord) {
        LogUtil.d("save", "test ${DebugUtil.tag()}")
        LogUtil.d("save", "vm 保存 开始-----------------")
        val olderMemoRecord = memoRecord.value
        LogUtil.d("save", "1 新旧记录对比")
        //记录存在memoRecord - 来自load - 新建没有null
        LogUtil.d("save", "2 旧记录 - OldMemoRecord = $olderMemoRecord")
        LogUtil.d("save", "3 新记录 - NewMemoRecord = $newMemoRecord")
        //没有旧记录 - 就插入
        if (olderMemoRecord == null) {
            //直接保存 memo
            //newMemoRecord.id = null //没有旧记录 - 没有id - 默认0
            saveMemo(newMemoRecord, false) //要刷新olderMemoRecord 存记录
            LogUtil.d("save", "7 没有老记录 保存记录")
        }
        //有旧记录 - 对比
        else {
            LogUtil.d("save", "3.5 老记录存在 id = ${olderMemoRecord.id}")
            if (olderMemoRecord.text == newMemoRecord.text) {
                if (olderMemoRecord.categoryId == newMemoRecord.categoryId
                    && olderMemoRecord.title == newMemoRecord.title
                ) {
                    LogUtil.d("save", "4 新老记录在标题 tag 正文(三者)都一样 / 不用保存到数据库")
                } else {
                    LogUtil.d(
                        "save",
                        "5 仅正文一样，需要需要保存 tag title - 到数据库 / 不需要加入历史"
                    )
                    newMemoRecord.id = olderMemoRecord.id
                    saveMemo(newMemoRecord, true)
                }
            } else {
                //正文不一样，保存到历史记录中
                LogUtil.d("save", "6 新老记录 正文不一样，保存记录 且 保存到历史")
                newMemoRecord.id = olderMemoRecord.id
                saveMemo(newMemoRecord, true)
                saveHistory(olderMemoRecord)
            }
        }

        LogUtil.d("save", "vm 保存 结束-----------------")
    }

    fun saveMemo(newMemoRecord: MemoRecord, isUpdate: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            when (isUpdate) {
                true -> {
                    LogUtil.e("save", "saveMemo 更新 到数据库前，新纪录，${newMemoRecord}")
                    val resRow = LocalRepository.updateOneMemoRecord(newMemoRecord)
                    LogUtil.e("save", "saveMemo 更新后，更新影响行数，resRow = $resRow")
                    memoRecord.postValue(newMemoRecord)
                }
                false -> {
                    LogUtil.e("save", "saveMemo 保存 到数据库前，新纪录，${newMemoRecord}")
                    val memoId = LocalRepository.saveOneMemoRecord(newMemoRecord)
                    LogUtil.e("save", "saveMemo 保存后，返回结果id，memoId = $memoId")
                    newMemoRecord.id = memoId //重置记录id，有无记录一个逻辑

                    saveHistory(newMemoRecord) //第一份也保存一下，否则修改成别的就改不回了

                    LogUtil.d(
                        "save",
                        "saveMemo 保存到数据库，更新记录为newMemoRecord = $newMemoRecord"
                    )
                    LogUtil.e("save", "saveMemo 保存到数据库，新纪录id，${newMemoRecord.id}")
                    memoRecord.postValue(newMemoRecord)
                }
            }
        }
    }

    private fun saveHistory(oldMemoRecord: MemoRecord) {
        //存在老记录
        viewModelScope.launch(Dispatchers.IO) {
            val historyMemo = History(
                memoId = oldMemoRecord.id,
                historyContent = oldMemoRecord.text,
                editTime = oldMemoRecord.lastEditTimeStamp,
            )
            val historyId = LocalRepository.saveOneHistory(historyMemo)
            historyMemo.id = historyId
            LogUtil.d(
                "save",
                "saveHistory 保存历史，historyMemo = $historyMemo | historyId = $historyId"
            )
        }
    }

    fun loadMemoRecord(memoId: Long) {
        viewModelScope.launch(Dispatchers.IO) {

            //1
            val memo = withContext(Dispatchers.IO) {
                LocalRepository.getOneMemoRecordById(memoId)
            }
            memoRecord.postValue(memo)

            //2
            categoryList.postValue(LocalRepository.getAllCategoryList())
        }
    }

    fun loadAudio(audioId: Long?) {
        if (audioId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                audio.postValue(LocalRepository.getOneAudioById(audioId))
            }
        }
    }

    fun setUri(uri: String) {
        audio.value?.uri = uri
        audio.value = audio.value //触发更新 player
        viewModelScope.launch(Dispatchers.IO) {
            // 保存

            //先查id
            if (audio.value!!.id <= 0) {
                // 先查有没有
                val audioResult = LocalRepository.getOneAudioByUri(uri)
                LogUtil.e(EditorFragment.TAG, "setUri - audioResult =  $audioResult")


                if (audioResult != null) { //查到 uri 所在 id
                    //有：更新id
                    audio.value?.id = audioResult.id
                } else { //没有查到id 且 没有 id
                    audio.value?.let {
                        //无：保存uri - 即音频
                        val resId = LocalRepository.saveOneAudio(it)
                        audio.value!!.id = resId
                    }
                }
            } else {
                //有 id 就更新 uri
                audio.value?.let {
                    val resId = LocalRepository.saveOneAudio(it)
                }
            }

        }
    }

    fun audioToText(context: Context) {
        //订单号不为空
        val orderId = audio.value!!.orderId

        if (orderId.isNotEmpty()) {
            //getResult 以该订单号 - 请求服务器
            //  请求 - 判断
            //轮询(订单存在状态且是否已完成)
            viewModelScope.launch {
                try {
                    val audioToTextResult =
                        RemoteRepository.getAudioToTextResult(
                            DateUtil.date().time.toString(),
                            orderId
                        )
                    if (audioToTextResult.getOrNull() != null) {
                        val transGetResult = audioToTextResult.getOrNull()!!
                        val code = transGetResult.code
                        if (code == ResultCode.SUCCESS.code) {
                            LogUtil.w(TAG, "-- responseBody.code = $code | 成功")

                            //预期时间初始值随便给,轮询时会自动调整
                            pollAudioOrderResults(orderId, 1000)

                        } else {
                            LogUtil.w(TAG, "++ responseBody.code = $code | 订单失效")
                            LogUtil.w(TAG, "++ responseBody.descInfo = ${transGetResult.descInfo}")
                            //订单失败，若uri存在则重传
                            throw OrderInvalidException("业务异常: 订单失效")
                        }

                    }
                } catch (e: OrderInvalidException) {
                    // 当订单无效时，直接进入处理音频Uri的逻辑
                    LogUtil.w(TAG, "++ 订单无效, OrderInvalidException->${e.message} ")
                    uploadAudio(context)
                }
            }
            //订单号 null ,进入 音频Uri处理逻辑
        } else {
            uploadAudio(context)
        }

    }

    private fun uploadAudio(context: Context) {

        val audioUri = audio.value!!.uri

        //Uri为空 or Uri代表文件不存在(无效)
        if (audioUri == null || !isFileExistsAtUri(context, audioUri.toUri())) {
            //* 通知没有文件，(解耦)更新liveData
            //* 可以通知，判断触发其他如对话框等UI更新
            //audioUri.value = audioUri.value //触发更新 -> if == null(要选择/录制文件)
            "文件为空或无效，请重新选择有效音频文件 或 录制音频文件".makeToast(Toast.LENGTH_LONG)
            isTranslating.postValue(false)
        }
        //音频Uri存在且有效
        else {
            //upload - audioUri
            //上传文件
            //1 上传准备: 获取文件及文件信息
            val audioFileInfo = AudioFileInfo(
                FileUtil.getFileName(context, audioUri.toUri()),
                FileUtil.getFileSize(context, audioUri.toUri()),
                FileUtil.getDurationMs(context, audioUri.toUri()),
                FileUtil.getTempFile(context, audioUri.toUri()),
                audioUri.toUri()
            )
            LogUtil.w(TAG, "上传文件信息 ->")
            LogUtil.w(TAG, "fileName -> ${audioFileInfo.fileName}")
            LogUtil.w(TAG, "fileSize -> ${audioFileInfo.fileSize}")
            LogUtil.w(TAG, "durationMs -> ${audioFileInfo.durationMs}")
            LogUtil.w(TAG, "tmpFile -> ${audioFileInfo.tmpFile}")

            //1.1 准备二进制音频文件
            val audioFileAtRequestBody: RequestBody =
                FileInputStream(audioFileInfo.tmpFile).readBytes()
                    .toRequestBody("application/octet-stream".toMediaTypeOrNull())
            //2 上传
            viewModelScope.launch(Dispatchers.IO) {

                val result = uploadAudioFile(
                    audioFileInfo.tmpFile.name,//传副本临时文件
                    audioFileInfo.fileSize,
                    audioFileInfo.durationMs,
                    DateUtil.date().time.toString(),
                    audioFileAtRequestBody
                )
                val transUploadResult = result.getOrNull()
                if (transUploadResult != null) {
                    LogUtil.w(TAG, "上传文件回调json -> 有结果")
                    if (transUploadResult.code == ResultCode.SUCCESS.code) {
                        LogUtil.w(TAG, "上传成功 code -> ${transUploadResult.code}")
                        LogUtil.w(TAG, "上传成功 descInfo -> ${transUploadResult.descInfo}")
                        LogUtil.w(TAG, "上传成功 transUploadResult -> $transUploadResult")

                        //保存订单号
                        audio.value!!.orderId = transUploadResult.content!!.orderId

                        viewModelScope.launch(Dispatchers.Main) {
                            setUri(audioUri) //触发更新 保存数据库
                        }

                        //删除临时文件
                        audioFileInfo.tmpFile.deleteOnExit()

                        //获取 - 有效orderId + 预估时间taskEstimateTime(ms)
                        //获取结果次数	不得超过100次

                        //upload?成功后轮询
                        pollAudioOrderResults(
                            transUploadResult.content!!.orderId,
                            transUploadResult.content!!.taskEstimateTime //可以自定义
                        )

                    } else {
                        LogUtil.w(TAG, "上传文件回调json -> 无结果")
                        LogUtil.w(TAG, "上传失败 code -> ${transUploadResult.code}")
                        LogUtil.w(TAG, "上传失败 descInfo -> ${transUploadResult.descInfo}")
                        "查看文件是否有效，录制音频文件错误，失败原因:${transUploadResult.descInfo}".makeToast(
                            Toast.LENGTH_LONG
                        )
                        isTranslating.postValue(false)
                    }
                } else {
                    isTranslating.postValue(false)
                    LogUtil.w(TAG, "result.getOrNull() == null 查看仓库类中方法")
                }
            }

        }
    }

    private suspend fun uploadAudioFile(
        fileName: String,
        fileSize: Long,
        duration: Long,
        timeStamp: String,
        //audioFile: MultipartBody.Part
        audioFile: RequestBody
    ) = RemoteRepository.uploadAudioFileForResult(
        fileName,
        fileSize,
        duration,
        timeStamp,
        audioFile
    )

    //轮询有效订单的结果
    private suspend fun pollAudioOrderResults(orderId: String, taskEstimateTime: Long) {
        // 一定时间，得到结果
        // 轮询结果 - 超出次数(总100) / 预估时间 - 如果是0，轮询次数改为1 （轮询次数）
        var pollingCount = 1 //只是粗略每次查询100次(查询次数递减)

        //先预算一下预估时间，再根据预估时间调整间距询问。
        // taskEstimateTime -> ms
        var taskEstimateTimeLeft = taskEstimateTime
        var stop = false

        while (pollingCount <= 50 && !stop) {
            LogUtil.w(TAG, "== 轮询[$pollingCount] ->")

            //轮询间隔 - 第一次是预估时间 - 二分递减
            //delay(calculateInterval(taskEstimateTime, pollingCount))

            delay(taskEstimateTimeLeft)

            val audioToTextResult = getAudioToTextResult(
                DateUtil.date().time.toString(),
                orderId
            )
            val result = audioToTextResult.getOrNull()
            if (result != null) {

                if (result.code == ResultCode.SUCCESS.code) { //保证有效oid在轮询期间有效

                    result.content?.let { content ->

                        when (content.orderInfo.status) {
                            OrderInfoCode.Status.ORDER_CREATED.code -> {
                                LogUtil.w(
                                    TAG,
                                    "== 轮询[$pollingCount] -> ${OrderInfoCode.Status.ORDER_CREATED.msg}"
                                )
                                taskEstimateTimeLeft = content.taskEstimateTime

                            }

                            OrderInfoCode.Status.ORDER_PROCESSING.code -> {
                                LogUtil.w(
                                    TAG,
                                    "== 轮询[$pollingCount] -> ${OrderInfoCode.Status.ORDER_PROCESSING.msg}"
                                )
                                taskEstimateTimeLeft = content.taskEstimateTime
                            }

                            OrderInfoCode.Status.ORDER_COMPLETED.code -> {
                                LogUtil.w(
                                    TAG,
                                    "== 轮询[$pollingCount] -> ${OrderInfoCode.Status.ORDER_COMPLETED.msg}"
                                )
                                //将转写的文字写到 保存的变量中 触发editor.insert

                                val textResult = getOrderResultText(content.orderResult)
                                //触发更新 observe
                                audioText.postValue(textResult)
                                audio.value!!.audioToText = textResult


                                viewModelScope.launch(Dispatchers.Main) {
                                    audio.value!!.uri?.let { setUri(it) } //触发更新 保存数据库
                                }


                                isTranslating.postValue(false) //刷新UI
                                stop = true //停止轮询
                            }

                            OrderInfoCode.Status.ORDER_FAILED.code -> {
                                LogUtil.w(
                                    TAG,
                                    "== 轮询[$pollingCount] -> ${OrderInfoCode.Status.ORDER_FAILED.msg}"
                                )

                                when (content.orderInfo.failType) {
                                    OrderInfoCode.FailType.AUDIO_NORMAL.code -> {
                                        LogUtil.w(
                                            TAG,
                                            "== 轮询[$pollingCount] -> ${OrderInfoCode.FailType.AUDIO_NORMAL.msg}"
                                        )
                                    }

                                    OrderInfoCode.FailType.UPLOAD_FAILED.code -> {
                                        LogUtil.w(
                                            TAG,
                                            "== 轮询[$pollingCount] -> ${OrderInfoCode.FailType.UPLOAD_FAILED.msg}"
                                        )
                                    }

                                    OrderInfoCode.FailType.TRANSCODING_FAILED.code -> {
                                        LogUtil.w(
                                            TAG,
                                            "== 轮询[$pollingCount] -> ${OrderInfoCode.FailType.TRANSCODING_FAILED.msg}"
                                        )
                                    }

                                    OrderInfoCode.FailType.RECOGNITION_FAILED.code -> {
                                        LogUtil.w(
                                            TAG,
                                            "== 轮询[$pollingCount] -> ${OrderInfoCode.FailType.RECOGNITION_FAILED.msg}"
                                        )
                                    }

                                    OrderInfoCode.FailType.DURATION_EXCEEDED.code -> {
                                        LogUtil.w(
                                            TAG,
                                            "== 轮询[$pollingCount] -> ${OrderInfoCode.FailType.DURATION_EXCEEDED.msg}"
                                        )
                                    }

                                    OrderInfoCode.FailType.INTEGRITY_CHECK_FAILED.code -> {
                                        LogUtil.w(
                                            TAG,
                                            "== 轮询[$pollingCount] -> ${OrderInfoCode.FailType.INTEGRITY_CHECK_FAILED.msg}"
                                        )
                                    }

                                    OrderInfoCode.FailType.SILENT_FILE.code -> {
                                        LogUtil.w(
                                            TAG,
                                            "== 轮询[$pollingCount] -> ${OrderInfoCode.FailType.SILENT_FILE.msg}"
                                        )
                                    }

                                    OrderInfoCode.FailType.OTHER_ERRORS.code -> {
                                        LogUtil.w(
                                            TAG,
                                            "== 轮询[$pollingCount] -> ${OrderInfoCode.FailType.OTHER_ERRORS.msg}"
                                        )
                                    }
                                }

                                "音频解析出错,错误信息：${OrderInfoCode.FailType.fromCode(content.orderInfo.failType)}"
                                isTranslating.postValue(false)
                                stop = true //停止轮询

                            }
                        }

                    }


                    //taskEstimateTimeLeft = result.content!!.taskEstimateTime

                }

            }


            pollingCount++

            if (taskEstimateTime == 0L) {
                break //0ms 说明上述代码已经处理完，防止轮询多度导致服务器阻塞
            }
        }


        //-------------------


    }

    private fun getOrderResultText(orderResultStr: String): String {
        val json = RetrofitCreator.json
        val orderResult = json.decodeFromString<OrderResult>(orderResultStr)
        val resultStr = StringBuilder()
        for (lattice2 in orderResult.lattice2) {
            val rts = lattice2.json1best.st.rt
            for (rt in rts) {
                val wsList = rt.ws
                for (ws in wsList) {
                    val word = ws.cw[0].w
                    resultStr.append(word)
                }
            }
        }
        return resultStr.toString()
    }
}