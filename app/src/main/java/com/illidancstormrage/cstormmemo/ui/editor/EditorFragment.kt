package com.illidancstormrage.cstormmemo.ui.editor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.LegacyPlayerControlView
import androidx.navigation.fragment.navArgs
import cn.hutool.core.date.DateUtil
import com.google.android.material.textfield.TextInputEditText
import com.hjq.permissions.XXPermissions
import com.illidancstormrage.csrich.toolitem.CSToolImage
import com.illidancstormrage.cstormmemo.R
import com.illidancstormrage.cstormmemo.databinding.FragmentEditorBinding
import com.illidancstormrage.cstormmemo.model.memo.MemoRecord
import com.illidancstormrage.cstormmemo.ui.SharedViewModel
import com.illidancstormrage.cstormmemo.ui.editor.dialog.HistoryDialogFragment
import com.illidancstormrage.cstormmemo.ui.record.AudioRecordActivity
import com.illidancstormrage.cstormmemo.utils.calendar.CalendarUtil.addEventToCalendar
import com.illidancstormrage.cstormmemo.utils.coroutines.launchAtIo
import com.illidancstormrage.cstormmemo.utils.extensions.pass
import com.illidancstormrage.cstormmemo.utils.extensions.requestReadCalendar
import com.illidancstormrage.cstormmemo.utils.extensions.requestWriteCalendar
import com.illidancstormrage.cstormmemo.utils.file.FileUtil
import com.illidancstormrage.utils.log.LogUtil
import com.illidancstormrage.utils.toast.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


@Suppress("DEPRECATION")
class EditorFragment : Fragment() {

    companion object {
        const val TAG = "EditorFragment"
        const val FROM_ALBUM = CSToolImage.FROM_ALBUM
        const val FROM_AUDIO_FILE = 1
        const val FROM_RECORD_AUDIO = 2
        const val APPLICATION_ID = "com.illidancstormrage.cstormmemo"
    }


    //viewBinding
    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    //viewModel
    private val editorViewModel by lazy { ViewModelProvider(this)[EditorViewModel::class.java] }
    private val sharedViewModel: SharedViewModel by activityViewModels()

    //navigate
    private val editorFragmentArgs: EditorFragmentArgs by navArgs()

    //menu
    private lateinit var menuProvider: MenuProvider

    //exoPlay
    private lateinit var player: ExoPlayer

    //playView
    private lateinit var playControlView: LegacyPlayerControlView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                save()
                isEnabled = false //禁用回调，否则回调死循序
                requireActivity().onBackPressed()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        val root = binding.root

        viewBinding()

        //viewModel - liveDataObserver
        liveDataObserver()

        initExoPlayer()

        return root
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun initExoPlayer() {
        player = ExoPlayer.Builder(requireContext()) //fragment不像activity，需要一些require函数
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
        playControlView.player = player
        playControlView.showTimeoutMs = Int.MAX_VALUE - 1
    }

    private fun viewBinding() {
        //写到菜单栏
        /*binding.btn.setOnClickListener {
            val action = EditorFragmentDirections.jumpToList()
            this@EditorFragment.findNavController().navigate(action)
        }*/


        //1 创建菜单项 menuProvider
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.edit_frag_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.save_memo -> {
                        save()
                    }

                    R.id.record_audio -> {
                        player.stop()
                        LogUtil.e(TAG, "recordAudio")
                        val intent = Intent(requireContext(), AudioRecordActivity::class.java)
                        startActivityForResult(intent, FROM_RECORD_AUDIO)
                    }

                    R.id.choose_audio -> {
                        player.stop()
                        //Intent.ACTION_PICK //一项
                        //打开文件选择器
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        //指定只音频
                        //action ACTION_OPEN_DOCUMENT，所以指定type为image/*，图片
                        intent.type = "audio/*"
                        startActivityForResult(intent, FROM_AUDIO_FILE)
                    }

                    R.id.audio_to_text -> {
                        //player.stop()
                        editorViewModel.isTranslating.value = true
                        editorViewModel.audioToText(requireActivity())
                        //requireActivity提供 contentResolver
                    }

                    R.id.memo_history -> {


                        val memoId = editorViewModel.memoRecord.value?.id
                        if (memoId != null) {
                            val dialog = HistoryDialogFragment()
                            dialog.arguments = Bundle().apply {
                                putLong("memoId", memoId)
                            }
                            dialog.setOnHistorySelectedListener { selectedHistory ->
                                binding.CSTextEditor.fromHtml(
                                    selectedHistory.historyContent //回调获取html值
                                )
                                val memoRecord = prepareMemoRecord()
                                memoRecord.lastEditTimeStamp = selectedHistory.editTime
                                memoRecord.id = editorViewModel.memoRecord.value!!.id
                                editorViewModel.saveMemo(memoRecord, true)
                            }
                            dialog.show(childFragmentManager, "history_dialog")
                        } else {
                            "第一次编辑文本，没有历史".makeToast()
                        }

                    }

                    R.id.memo_reminder -> {

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("添加提醒")
                        // 1
                        val editTextTitle = TextInputEditText(requireContext())
                        editTextTitle.hint = "标题"
                        editTextTitle.text = binding.title.text
                        // 2
                        val editTextDescription = TextInputEditText(requireContext())
                        editTextDescription.hint = "描述"

                        // 3
                        // 添加Spinner
                        var minutesBefore: Int = 0
                        val timeOptions = resources.getStringArray(R.array.time_options)
                        val spinner = Spinner(requireContext())
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            timeOptions
                        ).also { adapter ->
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner.adapter = adapter
                        }
                        // 设置Spinner的选中监听器
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    // 这里可以根据position获取选中的时间提前量，并转换为Calendar对象
                                    // 注意：此处需实现逻辑将选项转换为具体时间间隔，然后应用到Calendar对象
                                    minutesBefore = when (timeOptions[position]) {
                                        "准时" -> 0
                                        "5分钟前" -> 5
                                        "15分钟前" -> 15
                                        "30分钟前" -> 30
                                        "1小时前" -> 60
                                        "3小时前" -> 180
                                        "5小时前" -> 300
                                        "12小时前" -> 720
                                        "1天前" -> 1440
                                        else -> 0
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                    minutesBefore = 0
                                }
                            }

                        // 4. 添加日期选择按钮
                        val datePickerButtonStart = Button(requireContext())
                        datePickerButtonStart.text = "开始日期"
                        datePickerButtonStart.setOnClickListener {
                            showDatePickerDialog(1)
                        }
                        val datePickerButtonEnd = Button(requireContext())
                        datePickerButtonEnd.text = "结束日期"
                        datePickerButtonEnd.setOnClickListener {
                            showDatePickerDialog(2)
                        }

                        editorViewModel.startCalendar.observe(viewLifecycleOwner) {
                            datePickerButtonStart.text = it.time.toString()
                        }
                        editorViewModel.endCalendar.observe(viewLifecycleOwner) {
                            datePickerButtonEnd.text = it.time.toString()
                        }

                        val linearLayout = LinearLayout(this@EditorFragment.requireContext())
                        linearLayout.orientation = LinearLayout.VERTICAL
                        linearLayout.addView(editTextTitle)
                        linearLayout.addView(editTextDescription)
                        //开始时间
                        linearLayout.addView(TextView(requireContext()).also {
                            it.text = "开始时间"
                        })
                        linearLayout.addView(datePickerButtonStart)
                        //结束时间
                        linearLayout.addView(TextView(requireContext()).also {
                            it.text = "结束时间"
                        })
                        linearLayout.addView(datePickerButtonEnd)
                        //提醒
                        linearLayout.addView(TextView(requireContext()).also { it.text = "提醒" })
                        linearLayout.addView(spinner)
                        builder.setView(linearLayout)

                        // 添加更多视图或逻辑，如选择提前时间的UI

                        builder.setPositiveButton("确定") { dialog, which ->
                            val title = editTextTitle.text.toString()
                            val description = editTextDescription.text.toString()
                            "$title + $description + $minutesBefore".makeToast()

                            val fragment = this@EditorFragment
                            XXPermissions.with(context)
                                .requestWriteCalendar(fragment.requireContext())
                                .requestReadCalendar(fragment.requireContext()) {
                                    addEventToCalendar(
                                        fragment.requireContext(),
                                        editTextTitle.text.toString(),
                                        editTextDescription.text.toString(),
                                        editorViewModel.startCalendar.value!!, //需要dialog（inputEdit）设置用户设置描述
                                        editorViewModel.endCalendar.value!! //需要dialog（下拉框）设置选择提前时间
                                    )
                                }
                        }
                        builder.setNegativeButton("取消", null)
                        builder.show()
                    }

                    /*R.id.test_edit_menu_2 -> {
                        editorViewModel.isTranslating.value = true
                    }*/

                    android.R.id.home -> {
                        lifecycleScope.launchAtIo {
                            editorViewModel.pollAudioOrderResults("123",37536)
                        }
                    }
                }
                return true
            }

        }

        //2 播放器视图
        playControlView = binding.playerControlView

        //3 转圈
        binding.circleRefresh.setOnRefreshListener {
            if (editorViewModel.isTranslating.value == false) {
                binding.circleRefresh.isRefreshing = false
            }
        }
    }

    private fun showDatePickerDialog(choose: Int) {
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
                when (choose) {
                    1 -> editorViewModel.startCalendar.value = res
                    2 -> editorViewModel.endCalendar.value = res
                }
            },
            currentYear,
            currentMonth,
            currentDate
        )
        datePickerDialog.show()
    }

    private fun prepareMemoRecord(): MemoRecord {
        //0 audio保存已经在音频操作的时候同步到数据库了
        //audioId
        var audioId: Long? = 0L
        val audio = editorViewModel.audio.value
        audioId = if (audio != null) {
            if (audio.id > 0) {//id存在 即 >0
                audio.id
            } else {//id = 0L，
                null
            }
        } else { //audio不存在
            null
        }



        //1 保存 memo
        val memoRecord = MemoRecord(
            title = binding.title.text.toString(),
            text = binding.CSTextEditor.getHtml(),
            lastEditTimeStamp = DateUtil.date().time,
            categoryId = binding.categorySpinner.selectedItemId.run {
                if (this == AdapterView.INVALID_ROW_ID) {
                    //无效返回 默认值 - 不返回null
                    return@run 1
                }
                return@run (this + 1)
            }, //从下拉框中获得列表id
            audioId = audioId, //为0设置null
            //id = 0L //viewModel中判断
        )

        LogUtil.e("a","memoRecord = $memoRecord")

        return memoRecord
    }

    fun save() {

        if (binding.title.text.toString().isEmpty() && binding.CSTextEditor.isEmpty()) {
            return //标题 内容都为空，不保存
        }

        val memoRecord = prepareMemoRecord()
        LogUtil.e("save", "新笔记内容 newMemoRecord = $memoRecord")
        //2 保存 历史 - 在viewModel中

        //3 保存
        editorViewModel.saveMemoToDb(memoRecord)
    }

    private fun liveDataObserver() {

        editorViewModel.memoRecord.observe(viewLifecycleOwner) { memoRecord ->
            //将memo信息更新到编辑器中
            if (memoRecord != null) {
                //将memoRecord更新到UI上，并保存到ViewModel中 / 字段触发UI更新需要分开
                //标题
                binding.title.text.clear() //第二次触发更新 - 多添加一次标题
                binding.title.text.insert(0, memoRecord.title)
                //正文
                binding.CSTextEditor.fromHtml(memoRecord.text)
                //memoRecord 不为空，才能触发 audio的load加载
                editorViewModel.loadAudio(memoRecord.audioId)
            }
        }

        editorViewModel.categoryList.observe(viewLifecycleOwner) { categoryList ->
            //分类 - ById - 设置下拉框
            if (categoryList != null) {
                //1 Spinner的string数组数据
                val stringArray = ArrayList<String>()
                categoryList.forEach { category ->
                    stringArray.add(category.tagName)
                }
                //2 adapter
                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    stringArray
                )
                binding.categorySpinner.adapter = spinnerAdapter

                //3 onItemClickListener
                binding.categorySpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            //获取我们所选中的内容
                            val item = parent?.getItemAtPosition(position).toString();
                            //item.makeToast()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            //"没有选中任何东西".makeToast()
                        }
                    }

                val memo = editorViewModel.memoRecord.value
                /*if (memo != null) {
                    for ((i, category) in categoryList.withIndex()) {
                        if (category.id == memo.categoryId) {
                            for ((j, str) in stringArray.withIndex()) {
                                if (category.tagName == str) {
                                    binding.categorySpinner.setSelection(j)
                                }
                            }
                            break
                        }
                    }
                }*/
                memo?.let { _ ->
                    val categoryIdMatch = categoryList.firstOrNull { it.id == memo.categoryId }
                    categoryIdMatch?.tagName?.let { tagName ->
                        val index = stringArray.indexOfFirst { it == tagName }
                        if (index != -1) {
                            binding.categorySpinner.setSelection(index)
                        }
                    }
                }

            }
        }

        editorViewModel.audio.observe(viewLifecycleOwner) { audio ->
            player.stop()
            //播放器 - 音频 - ById
            audio.uri?.let { uri ->

                LogUtil.e(TAG, "audio.observe 结果 audio = $audio")

                if (uri.isNotEmpty()) {
                    if (FileUtil.isFileExistsAtUri(requireContext(), Uri.parse(audio.uri))) {
                        binding.playerControlView.visibility = View.VISIBLE
                        player.setMediaItem(MediaItem.fromUri(uri))
                        player.prepare()
                    } else {
                        binding.playerControlView.visibility = View.GONE
                        player.stop()
                    }
                }
            }

        }

        editorViewModel.isTranslating.observe(viewLifecycleOwner) { isNeedReFresh ->
            binding.circleRefresh.isRefreshing = isNeedReFresh
        }

        editorViewModel.audioText.observe(viewLifecycleOwner) { audioText ->
            val editor = binding.CSTextEditor
            editor.insertTextAtEnd(audioText)
        }

        sharedViewModel.data.observe(viewLifecycleOwner) { data ->
            binding.CSTextEditor.onActivityResult(data)
            //传完要清空否则插入图片会不断传入
            data.data = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 1 设置菜单
        // 将MenuProvider添加到Activity的MenuHost
        requireActivity().addMenuProvider(
            menuProvider,
            viewLifecycleOwner,   // 这使得MenuProvider的生命周期与Fragment的视图生命周期绑定
            Lifecycle.State.RESUMED // 只有当Fragment处于RESUMED状态时，菜单才可见
        )

        // init Data
        // 2 加载笔记内容 (获取从导航来的数据 - 笔记id)
        val memoId = editorFragmentArgs.memoId //默认值 = 0L
        editorViewModel.loadMemoRecord(memoId)

        editorViewModel.isTranslating.value = false
    }


    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()

        //LogUtil.v(TAG, "edit _ test _ onResume")
        //LogUtil.w(TAG, this.findNavController().currentBackStackEntry.toString())
        //LogUtil.w(TAG, this.findNavController().currentBackStack.value.toString())
        //LogUtil.v(TAG, "edit _ test _ onResume")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.fragment.app.Fragment"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FROM_ALBUM -> { //在fragment不起作用(需要MainActivity传递/待修改底层组件代码/使用shared将就用)
                if (resultCode == Activity.RESULT_OK) {
                    LogUtil.n(TAG, "onActivityResult: FROM_ALBUM 打开相册")
                    binding.CSTextEditor.onActivityResult(data)
                }
            }

            FROM_AUDIO_FILE -> {
                if (resultCode == Activity.RESULT_OK) {
                    LogUtil.e(TAG, "FROM_AUDIO_FILE ")
                    data?.data?.let { uri ->
                        binding.playerControlView.visibility = View.VISIBLE

                        requireActivity().contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        //触发播放器更新
                        editorViewModel.setUri(uri.toString())
                        //天下授权uri
                        //content://com.android.providers.media.documents/document/audio%3A1000170176
                        //加载交给observe
                        //player.setMediaItem(MediaItem.fromUri(audioUri))
                        //player.prepare()
                    }
                }
            }

            FROM_RECORD_AUDIO -> {
                if (resultCode == Activity.RESULT_OK) {
                    val filePathName = data?.let { intent ->
                        //binding.PlayerControlView.visibility = View.VISIBLE
                        "".pass()
                        intent.getStringExtra(AudioRecordActivity.EXTRA_AUDIO_RESULT)
                    }
                    LogUtil.e(TAG, "FROM_RECORD_AUDIO 返回结果 $filePathName")
                    // /storage/emulated/0/Android/data/com.illidancstormrage.spandemo/cache/audioRecord_2024_04_29_20_28_17.m4a
                    val audioRecordFile = File(filePathName!!)
                    //val audioRecordFile = File(getExternalCacheDir(), "audioRecord_2024_04_26_17_19_35.m4a")
                    //audioUri = Uri.fromFile(audioRecordFile) //ok

                    editorViewModel.setUri(
                        FileProvider.getUriForFile(
                            requireContext(),
                            "${APPLICATION_ID}.fileprovider",
                            audioRecordFile
                        ).toString()
                    )



                    LogUtil.e(
                        TAG,
                        "editorViewModel.audio.value?.uri 返回结果 ${editorViewModel.audio.value?.uri}"
                    )
                    //player.setMediaItem(MediaItem.fromUri(audioUri))
                    //player.prepare()

                    //来自私有目录文件，使用 -
                    //法1：FileProvider - 允许共享的文件路径file_paths.xml
                    //法2：流 - 操作文件
                }
            }
        }
    }


}