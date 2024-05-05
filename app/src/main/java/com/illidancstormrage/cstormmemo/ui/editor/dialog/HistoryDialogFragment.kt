package com.illidancstormrage.cstormmemo.ui.editor.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.illidancstormrage.cstormmemo.databinding.DialogHistoryBinding
import com.illidancstormrage.cstormmemo.model.history.History
import com.illidancstormrage.cstormmemo.ui.editor.dialog.adapter.HistoryListAdapter
import com.illidancstormrage.utils.log.LogUtil

class HistoryDialogFragment : DialogFragment {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    private lateinit var binding: DialogHistoryBinding
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var adapter: HistoryListAdapter
    private val historyViewModel by lazy {
        ViewModelProvider(this)[HistoryViewModel::class.java]
    }

    //fun interface - 提供SAM lambda格式
    fun interface OnHistorySelectedListener {
        fun onHistorySelected(selectedHistory: History)
    }

    private lateinit var mHistorySelectedListener: OnHistorySelectedListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogHistoryBinding.inflate(inflater, container, false)

        //绑定变量，监听器得到那个
        viewBinding()

        //viewModel - 初始化 + 绑定观察者
        liveDataObserver()


        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun liveDataObserver() {
        arguments?.let {
            LogUtil.e("test", "对话框接收值 memoId = ${it.getLong("memoId")}")
            historyViewModel.loadHistoryList(it.getLong("memoId")) // 传值
        }


        //触发删除后更新UI
        historyViewModel.historiesByMemo.observe(viewLifecycleOwner) {
            LogUtil.e("save", "对话框adapter - ${it.toString()}")
            if (it?.histories != null) {
                adapter = HistoryListAdapter(this, it.histories, historyViewModel)
                historyRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        //选中历史绑定逻辑
        historyViewModel.history.observe(viewLifecycleOwner) {
            LogUtil.e("test", it.toString())
            if (it != null) {
                mHistorySelectedListener.onHistorySelected(it)
                // 确保在实际需要关闭的时候调用dismiss
                dismiss()
            }
        }
    }

    private fun viewBinding() {
        historyRecyclerView = binding.historyRecycleView

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //LogUtil.e(TAG, "$TAG - onViewCreated")

        //绑定adapter
        loadHistoryListRecycleViewAdapter()

    }

    private fun loadHistoryListRecycleViewAdapter() {
        val layoutManager = LinearLayoutManager(activity)
        historyRecyclerView.layoutManager = layoutManager
    }

    fun setOnHistorySelectedListener(callback: OnHistorySelectedListener) {
        mHistorySelectedListener = callback
        //这里 dismiss() 会报错，放到observe中
    }
}

