package com.illidancstormrage.cstormmemo.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.illidancstormrage.cstormmemo.databinding.ItemMemoListBinding
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.cstormmemo.ui.search.SearchFragment
import com.illidancstormrage.cstormmemo.ui.search.SearchFragmentDirections
import com.illidancstormrage.cstormmemo.ui.search.SearchViewModel
import com.illidancstormrage.cstormmemo.utils.extensions.msFormatDateStr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoBySearchAdapter(
    private val fragment: SearchFragment,
    private val searchList: List<MemoRecord>,
    private val searchViewModel: SearchViewModel
) : RecyclerView.Adapter<MemoBySearchAdapter.ViewHolder>() {
    inner class ViewHolder(private var binding: ItemMemoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: MemoRecord) {
            binding.memoTitle.text = record.title
            binding.memoLastTime.text = record.lastEditTimeStamp.msFormatDateStr()
            CoroutineScope(Dispatchers.IO).launch {
                if (record.categoryId != null) {
                    val tagName =
                        LocalRepository.categoryDao.selectById(record.categoryId!!).tagName
                    launch(Dispatchers.Main) {
                        binding.memoTag.text = tagName
                    }
                } else {
                    launch(Dispatchers.Main) {
                        binding.memoTag.text = "无"
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ItemMemoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)

        // holder.absoluteAdapterPosition //不管动画导致删除插入，还是改变可见性，返回list中绝对位置
        // holder.bindingAdapterPosition//与上相反，获取可视化中相对绑定位置
        // 设置 ItemMemoListBinding 样式 - 保存到holder

        holder.itemView.setOnClickListener {
            val action =
                SearchFragmentDirections.jumpToEditFromSearch(searchList[holder.absoluteAdapterPosition].id)
            fragment.findNavController().navigate(action)
        }

        return holder
    }

    override fun getItemCount() = searchList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memoRecord = searchList[position]
        holder.bind(memoRecord)
    }
}