package com.illidancstormrage.cstormmemo.ui.record

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.hutool.core.date.DateUtil
import com.hjq.permissions.XXPermissions
import com.illidancstormrage.cstormmemo.databinding.ActivityAudioRecordBinding
import com.illidancstormrage.cstormmemo.utils.extensions.requestRecordAudio
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


class AudioRecordActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AudioRecordActivity"
        const val EXTRA_AUDIO_RESULT = "extra_audio_result"
    }


    private var fileName = ""

    private lateinit var binding: ActivityAudioRecordBinding


    //录制器
    private var recorder: MediaRecorder? = null

    //是否正在录制
    private var isRecording: Boolean = false
    private lateinit var startButton: Button

    //viewModel
    private val audioRecordViewModel by lazy {
        ViewModelProvider(this)[AudioRecordViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindListenerToView()
        //设置录制后文件名称 - timeID
        val date = DateUtil.date()
        val fileTimeIDName = DateUtil.format(date, "yyyy_MM_dd_HH_mm_ss")
        fileName = "${externalCacheDir?.absolutePath}/audioRecord_$fileTimeIDName.m4a"

    }

    @SuppressLint("SetTextI18n")
    private fun bindListenerToView() {
        binding.recordStartPause.setOnClickListener {

            XXPermissions.with(this).requestRecordAudio(this){
                startRecording()
            }
        }
        binding.recordStop.setOnClickListener {
            stopRecording()
        }

        //更新 录制时间UI
        audioRecordViewModel.durationTimeMs.observe(this) { durationTimeMs ->
            if (durationTimeMs == 0) {
                binding.recordDuration.text = "00:00:00"
                return@observe
            }
            val seconds = durationTimeMs / 1000 % 60
            val minutes = durationTimeMs / (1000 * 60) % 60
            val hours = durationTimeMs / (1000 * 60 * 60) % 24
            val durationTimeView = String.format("%d:%02d:%02d", hours, minutes, seconds)
            binding.recordDuration.text = durationTimeView
        }

        startButton = binding.recordStartPause
    }


    private fun startRecording() {
        // MediaRecorder 不支持暂停
        if (!isRecording) { //未开始录制
            isRecording = true
            startButton.isEnabled = false //不可点击
            startButton.text = "正在录制"

            audioRecordViewModel.clearDurationView()
            //recorder + start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                recorder = MediaRecorder(this).apply {
                    // 设置音频源为麦克风 MIC
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    // 设置音频输出格式
                    // https://developer.android.com/media/platform/supported-formats?hl=zh-cn
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //m4a等
                    setOutputFile(fileName)
                    // 设置音频编码格式
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB) //AAC
                    try {
                        prepare()
                    } catch (e: IOException) {
                        Log.e(TAG, "prepare() failed")
                    }
                    start()
                }

                //时间开始
                audioRecordViewModel.addOneSecond()

            } else {
                recorder = MediaRecorder().apply {
                    // 设置音频源为麦克风 MIC
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    // 设置音频输出格式
                    // https://developer.android.com/media/platform/supported-formats?hl=zh-cn
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //m4a等
                    setOutputFile(fileName)
                    // 设置音频编码格式
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB) //AAC
                    try {
                        prepare()
                    } catch (e: IOException) {
                        Log.e(TAG, "prepare() failed")
                    }
                    start()
                }

                //时间开始
                audioRecordViewModel.addOneSecond()
            }
            //协程每1s，更新一次
            lifecycleScope.launch {
                while (isRecording) {
                    delay(1000)
                    audioRecordViewModel.addOneSecond()
                }
            }
        }
    }

    private fun stopRecording() {
        isRecording = false
        startButton.text = "开始"
        audioRecordViewModel.clearDurationView()
        recorder?.apply {
            stop()
            release()
        }
        recorder = null


        //转到 AudioPlayerActivity
        val intent = Intent().apply {
            putExtra(EXTRA_AUDIO_RESULT, fileName) //别出错
        }
        setResult(RESULT_OK, intent)
        finish()
    }


}