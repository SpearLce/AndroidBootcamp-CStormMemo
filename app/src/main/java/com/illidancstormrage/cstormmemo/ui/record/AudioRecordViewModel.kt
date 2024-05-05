package com.illidancstormrage.cstormmemo.ui.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AudioRecordViewModel : ViewModel() {

    //1 定义LiveData，并初始化（三种）
    //1.1
    var durationTimeMs = MutableLiveData(0)

    //1.2
    //init {
    //    durationTimeMs.value = 0 //初始化
    //}
    //1.3
    //另一个来自仓库的liveData，switchUp

    //2 配置set get等更新方法
    fun addOneSecond() {
        durationTimeMs.value = durationTimeMs.value?.plus(1000)
    }

    fun clearDurationView() {
        durationTimeMs.value = 0
    }

    //-----------------------------------------------------


}
