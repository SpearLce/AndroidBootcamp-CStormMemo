package com.illidancstormrage.cstormmemo.ui.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.utils.log.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    //缓存列表数据 - 需要liveData观察
    val memoList = MutableLiveData<List<MemoRecord>>()

    init {
        LogUtil.w("ListViewModel","ListViewModel - init")
    }

    fun loadMemoList() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1500)//等数据表加载完
            LogUtil.w("ListViewModel","getAllMemoListByDesc -> ${LocalRepository.getAllMemoListByDesc()}")
            memoList.postValue(LocalRepository.getAllMemoListByDesc()) //异步将数据post更新出去，触发观察
        }
    }
}