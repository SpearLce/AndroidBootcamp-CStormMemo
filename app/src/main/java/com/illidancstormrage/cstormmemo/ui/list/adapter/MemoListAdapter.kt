package com.illidancstormrage.cstormmemo.ui.list.adapter


import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.illidancstormrage.cstormmemo.databinding.ItemMemoListBinding
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.cstormmemo.ui.list.ListFragment
import com.illidancstormrage.cstormmemo.ui.list.ListFragmentDirections
import com.illidancstormrage.cstormmemo.ui.list.ListViewModel
import com.illidancstormrage.cstormmemo.utils.extensions.msFormatDateStr
import com.illidancstormrage.utils.toast.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoListAdapter(
    //提供环境
    private val fragment: ListFragment,
    //提供列表数据源
    private val memoRecordList: List<MemoRecord>,
    private val listViewModel: ListViewModel
) : RecyclerView.Adapter<MemoListAdapter.ViewHolder>() {


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

        //短按
        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            //此处还是过时了，那么绑定在onBindViewHolder相关处
            //"[$position] -> ${memoRecordList[position].title}".makeToast()

            //更新 editor_fragment_viewModel中
            //val editorViewModel =
            val action = ListFragmentDirections.jumpToEdit(memoRecordList[position].id)
            it.findNavController().navigate(action)
        }
        //长按
        holder.itemView.setOnLongClickListener { view ->
            val position = holder.bindingAdapterPosition
            val alertDialog = AlertDialog.Builder(view.context)
                .setTitle("注意")
                .setMessage("是否确定删除该记录")
                .setPositiveButton("确定") { dialog, _ ->
                    listViewModel.deleteMemo(memoRecordList[position].id)
                    dialog.dismiss()
                }.setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true//消费长按事件，不会传递到其他长按绑定事件
        }


        return holder
    }

    override fun getItemCount() = memoRecordList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memoRecord = memoRecordList[position]
        holder.bind(memoRecord)
    }

}