package com.illidancstormrage.cstormmemo.ui.editor.dialog.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.illidancstormrage.cstormmemo.databinding.ItemHistoryListBinding
import com.illidancstormrage.cstormmemo.model.history.History
import com.illidancstormrage.cstormmemo.repository.LocalRepository
import com.illidancstormrage.cstormmemo.ui.editor.dialog.HistoryViewModel
import com.illidancstormrage.cstormmemo.ui.editor.dialog.show.ShowActivity
import com.illidancstormrage.cstormmemo.utils.extensions.msFormatDateStr
import com.illidancstormrage.utils.toast.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryListAdapter(
    val dialogFragment: DialogFragment,
    private val historyList: List<History>,
    private val historyViewModel: HistoryViewModel
) : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemHistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(history: History) {
            binding.deleteHistory.setOnClickListener {
                historyViewModel.deleteHistory(history)
            }
            binding.previewHistory.setOnClickListener {
                val intent = Intent(dialogFragment.requireContext(),ShowActivity::class.java)
                intent.putExtra("html",history.historyContent)
                dialogFragment.startActivity(intent)
            }
            binding.okHistory.setOnClickListener {
                historyViewModel.selectHistory(history)
            }
            binding.timeHistory.text = history.editTime.msFormatDateStr()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = historyList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(historyList[position])
    }
}
