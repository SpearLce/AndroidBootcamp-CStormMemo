package com.illidancstormrage.cstormmemo.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.illidancstormrage.cstormmemo.databinding.FragmentSearchBinding
import com.illidancstormrage.cstormmemo.ui.editor.EditorFragment
import com.illidancstormrage.utils.log.LogUtil

class SearchFragment : Fragment() {
    companion object{
        private const val TAG = "SearchFragment"
    }



    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root = binding.root

        liveDataObserver()

        return root
    }

    private fun liveDataObserver() {

    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        LogUtil.v(TAG, "search _ test _ onResume")
        LogUtil.w(TAG, this.findNavController().currentBackStackEntry.toString())
        LogUtil.w(TAG, this.findNavController().currentBackStack.value.toString())
        LogUtil.v(TAG, "search _ test _ onResume")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }
}