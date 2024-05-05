package com.illidancstormrage.cstormmemo.ui.editor.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illidancstormrage.cstormmemo.model.history.History
import com.illidancstormrage.cstormmemo.model.memo.MemoWithHistories
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.utils.toast.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    val historiesByMemo = MutableLiveData<MemoWithHistories>()
    val history = MutableLiveData<History>()

    fun loadHistoryList(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            historiesByMemo.postValue(LocalRepository.getHistoriesByMemoId(id))
        }
    }

    fun deleteHistory(history: History) {
        viewModelScope.launch(Dispatchers.IO) {
            val resRow = LocalRepository.deleteOneHistory(history)
            if (resRow > 0) {
                "删除成功 id = ${history.id}".makeToast()
                val listDeleted = historiesByMemo.value!!.histories.filterNot {
                    it == history
                }
                historiesByMemo.value!!.histories = listDeleted
                historiesByMemo.postValue(historiesByMemo.value) //触发更新
            } else {
                "删除失败 $history".makeToast()
            }

        }
    }

    fun selectHistory(history: History){
        this.history.value = history
    }

}