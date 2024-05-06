package com.illidancstormrage.cstormmemo.ui.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.illidancstormrage.cstormmemo.R
import com.illidancstormrage.cstormmemo.databinding.FragmentListBinding
import com.illidancstormrage.cstormmemo.ui.editor.EditorFragment
import com.illidancstormrage.cstormmemo.ui.list.adapter.MemoListAdapter
import com.illidancstormrage.utils.log.LogUtil

class ListFragment : Fragment() {

    companion object {
        private const val TAG = "ListFragment"
    }

    private var _binding: FragmentListBinding? = null

    //binding
    private val binding get() = _binding!!

    //viewModel
    private val listViewModel by lazy { ViewModelProvider(this)[ListViewModel::class.java] }
    //private val listViewModel by navGraphViewModels<ListViewModel>(R.id.mobile_navigation)

    //adapter
    private lateinit var adapter: MemoListAdapter

    private lateinit var memoListRecycleView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //LogUtil.e(TAG, "$TAG - onCreateView")

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root = binding.root

        //绑定变量，监听器得到那个
        viewBinding()

        //viewModel - 初始化 + 绑定观察者
        liveDataObserver()

        return root
    }

    private fun viewBinding() {
        memoListRecycleView = binding.memoListRecycleView


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun liveDataObserver() {
        //onCreateView
        //数据库加载数据
        listViewModel.loadMemoList() //异步加载

        listViewModel.memoList.observe(viewLifecycleOwner) {
            LogUtil.e(TAG, "$TAG 列表memoList被触发更新 = $it")
            adapter = MemoListAdapter(this, listViewModel.memoList.value!!,listViewModel)
            memoListRecycleView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //LogUtil.e(TAG, "$TAG - onViewCreated")

        //绑定adapter
        loadMemoListRecycleViewAdapter()

    }

    private fun loadMemoListRecycleViewAdapter() {
        val layoutManager = LinearLayoutManager(activity)
        memoListRecycleView.layoutManager = layoutManager
    }


    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()

        listViewModel.loadMemoList()

        //查看栈
        //LogUtil.d(TAG, "list 当前状态可见(resume)")
        //LogUtil.v(TAG, "list _ test _ onResume")
        //LogUtil.w(TAG, this.findNavController().currentBackStackEntry.toString())
        //LogUtil.w(TAG, this.findNavController().currentBackStack.value.toString())
        //LogUtil.v(TAG, "list _ test _ onResume")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //LogUtil.e(TAG, "$TAG - onDestroyView")
        _binding = null
    }


}