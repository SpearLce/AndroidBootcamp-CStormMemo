package com.illidancstormrage.cstormmemo.ui.search

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.contains
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenResumed
import androidx.lifecycle.withResumed
import androidx.recyclerview.widget.LinearLayoutManager
import com.illidancstormrage.cstormmemo.R
import com.illidancstormrage.cstormmemo.databinding.FragmentSearchBinding
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.cstormmemo.ui.search.adapter.MemoBySearchAdapter
import com.illidancstormrage.utils.log.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    companion object {
        private const val TAG = "SearchFragment"
    }

    //
    private lateinit var categorySpinner: Spinner
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }
    private lateinit var adapter: MemoBySearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root = binding.root

        viewBinding()

        liveDataObserver()

        return root
    }

    private fun viewBinding() {
        //下拉框
        // 1 adapter数据源
        val searchCategories = resources.getStringArray(R.array.search_categories)
        // 2 spinner
        //val spinner = Spinner(requireContext())
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            searchCategories
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.searchSpinner.adapter = adapter
        }
        // 3 设置Spinner的选中监听器
        binding.searchSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // 这里可以根据position获取选中的时间提前量，并转换为Calendar对象
                    // 注意：此处需实现逻辑将选项转换为具体时间间隔，然后应用到Calendar对象
                    when (searchCategories[position]) {
                        "标题" -> {
                            searchViewModel.tag = "标题"
                            searchViewModel.searchList.value = ArrayList()//[] - 清空
                            binding.searchInputView.visibility = View.VISIBLE
                            if (this@SearchFragment::categorySpinner.isInitialized) {
                                binding.searchBar.removeView(categorySpinner)
                            }
                            if (!binding.searchBar.contains(binding.searchInputView)) {
                                binding.searchBar.addView(binding.searchInputView)
                            }
                        }

                        "分类" -> {
                            searchViewModel.tag = "分类"
                            searchViewModel.searchList.value = ArrayList()//[] - 清空
                            binding.searchInputView.visibility = View.GONE
                            //binding.searchBar.removeView(binding.searchInputView)
                            addCategorySpinner()
                        }

                        "日期" -> {
                            searchViewModel.tag = "日期"
                            searchViewModel.searchList.value = ArrayList()//[] - 清空
                            binding.searchInputView.visibility = View.GONE
                            if (this@SearchFragment::categorySpinner.isInitialized) {
                                binding.searchBar.removeView(categorySpinner)
                            }
                            //日期对话框
                            showDatePickerDialog()
                        }

                        else -> {}
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    searchViewModel.tag = "标题" //默认标题
                }
            }

        //搜索框
        //  SearchView - android.widget.SearchView
        binding.searchInputView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 当用户提交查询时会被调用，你可以在这里执行实际的搜索操作
                if (query != null) {
                    // 执行查询逻辑
                    //performSearch(query)
                    LogUtil.e(TAG, "query = $query")
                }
                return true // 返回true表示你已经处理了提交事件，不需要进一步的默认处理
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 当查询文本改变时会被调用，可以在这里实时更新查询结果
                if (newText != null) {
                    // 实时查询逻辑，例如过滤列表数据
                    //filterResults(newText)
                    LogUtil.e(TAG, "newText = $newText")
                    if (newText.isNotEmpty()) {
                        searchViewModel.search(newText)

                    } else {
                        //清空list
                        searchViewModel.search("null")
                    }
                }
                return true // 同样，返回true表示你已经处理了文本变化事件
            }
        })
        //setOnQueryTextFocusChangeListener - 获得或失去焦点）时做一些UI或状态的调整
        //setOnQueryTextListener - 查询文本变化以执行搜索逻辑


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun liveDataObserver() {
        searchViewModel.searchList.observe(viewLifecycleOwner) {
            //it 可传 [] 空集合
            adapter = MemoBySearchAdapter(this, it, searchViewModel)
            binding.searchListRecycleView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        searchViewModel.searchCalendar.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                withResumed {}
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    // 这个block会在Lifecycle变为RESUMED状态时执行，
                    // 并在Lifecycle离开RESUMED状态时自动取消。
                    // 你可以在此处执行你的网络请求或其他耗时操作。
                    // 注意：这个block是非阻塞的，应使用挂起函数。
                    val res = async(Dispatchers.IO) {
                        LocalRepository.getMemoListByDate(it)
                    }
                    adapter = MemoBySearchAdapter(this@SearchFragment, res.await(), searchViewModel)
                    binding.searchListRecycleView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun addCategorySpinner() {
        lifecycleScope.launch {
            categorySpinner = Spinner(requireContext())
            val categoriesDeferred = async(Dispatchers.IO) {
                LocalRepository.getAllCategoryList()
            }
            val categories = categoriesDeferred.await()
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories.map { it.tagName }
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = adapter
            }
            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    //viewModel
                    val idText = categories[position].id.toString()
                    searchViewModel.search(idText)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    val idText = categories[0].id.toString()
                    searchViewModel.search(idText)
                }
            }
            binding.searchBar.addView(categorySpinner)
        }
    }

    private fun showDatePickerDialog() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        // 创建并显示DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // 用户选择了日期后的回调 -- 异步回调出去
                val res = Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth)
                }
                // 采用其他通信机制
                // ViewModel中的LiveData、SharedFlow（如果使用Compose）、或者直接在OnDateSetListener中调用一个预先定义好的回调函数
                searchViewModel.searchCalendar.value = res
            },
            currentYear,
            currentMonth,
            currentDate
        )
        datePickerDialog.show()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //adapter
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.searchListRecycleView.layoutManager = linearLayoutManager

    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()

        //隐藏toolbar
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }
}