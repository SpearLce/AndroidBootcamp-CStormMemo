package com.illidancstormrage.cstormmemo.ui.list.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.illidancstormrage.cstormmemo.databinding.ItemMemoListBinding
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.cstormmemo.ui.list.ListFragment
import com.illidancstormrage.cstormmemo.ui.list.ListFragmentDirections
import com.illidancstormrage.cstormmemo.utils.extensions.msFormatDateStr
import com.illidancstormrage.utils.toast.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoListAdapter(
    //提供环境
    private val fragment: ListFragment,
    //提供列表数据源
    private val memoRecordList: List<MemoRecord>
) : RecyclerView.Adapter<MemoListAdapter.ViewHolder>() {


    inner class ViewHolder(private var binding: ItemMemoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: MemoRecord) {
            binding.memoTitle.text = record.title
            binding.memoLastTime.text = record.lastEditTimeStamp.msFormatDateStr()
            CoroutineScope(Dispatchers.IO).launch {
                if (record.categoryId != null) {
                    val tagName = LocalRepository.categoryDao.selectById(record.categoryId!!).tagName
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

        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            "[$position] -> ${memoRecordList[position].title}".makeToast()

            //更新 editor_fragment_viewModel中
            //val editorViewModel =
            val action = ListFragmentDirections.jumpToEdit(memoRecordList[position].id!!)
            it.findNavController().navigate(action)
        }


        return holder
    }

    override fun getItemCount() = memoRecordList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memoRecord = memoRecordList[position]
        holder.bind(memoRecord)
    }

}