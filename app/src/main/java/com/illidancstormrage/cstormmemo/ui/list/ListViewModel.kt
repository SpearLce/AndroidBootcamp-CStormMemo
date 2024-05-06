package com.illidancstormrage.cstormmemo.ui.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.utils.log.LogUtil
import com.illidancstormrage.utils.toast.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    //缓存列表数据 - 需要liveData观察
    val memoList = MutableLiveData<List<MemoRecord>>()

    init {
        LogUtil.w("ListViewModel", "ListViewModel - init")
    }

    fun loadMemoList() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)//等数据表加载完
            memoList.postValue(LocalRepository.getAllMemoListByDesc()) //异步将数据post更新出去，触发观察
        }
    }

    fun deleteMemo(memoId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val memoDel = MemoRecord(
                title = "",
                text = "",
                lastEditTimeStamp = 0,
                categoryId = null,
                audioId = null,
                id = memoId
            )
            val resRow = LocalRepository.deleteOneMemoRecord(memoDel)
            if (resRow > 0) {
                "删除成功".makeToast()
            } else {
                "删除失败".makeToast()
            }
            loadMemoList()//触发更新 - 其中有postValue
        }
    }
}