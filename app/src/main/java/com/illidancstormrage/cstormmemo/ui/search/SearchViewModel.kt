package com.illidancstormrage.cstormmemo.ui.search

import android.icu.util.Calendar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.utils.log.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    companion object{
        private const val TAG = "SearchViewModel"
    }

    // 保存 搜索选项
    var tag = ""
    val searchList = MutableLiveData<List<MemoRecord>>()
    val searchCalendar = MutableLiveData<Calendar>()
    fun search(searchText: String) {
        viewModelScope.launch(Dispatchers.IO) {

            when (tag) {
                "标题" -> {
                    val memoRecordList = LocalRepository.getMemoListByTitleLike(searchText)
                    LogUtil.e(TAG,"搜索结果 = $memoRecordList")
                    searchList.postValue(memoRecordList)
                }

                "分类" -> {
                    val memoRecordList = LocalRepository.getOneMemoRecordByCategoryId(searchText.toLong())
                    searchList.postValue(memoRecordList)
                }

                "日期" -> {

                }
            }
        }
    }
}