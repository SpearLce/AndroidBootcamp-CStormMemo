package com.illidancstormrage.csrich.richeditor

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.setPadding
import com.illidancstormrage.csrich.toolitem.CSToolItem
import com.illidancstormrage.csrich.utils.htmlconvert.CSHtml
import com.illidancstormrage.csrich.utils.htmlconvert.CSImageGetter
import com.illidancstormrage.csrich.utils.htmlconvert.CSTagHandler
import com.illidancstormrage.utils.log.LogUtil


class CSEditText : AppCompatEditText {

    companion object {
        private const val TAG = "CSEditText"
    }


    //工具栏 - 工具项集合
    private lateinit var tools: List<CSToolItem>


    //布尔 - 编辑状态（避免死循环）
    //true
    private var editable = true


    private var context: Context? = null

    //构造函数-----------------------------------------------------------------------------------
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        //初始化CSEditText
        initView()
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : super(context, null, 0) {
        //用super - 这里不能用this，因为kotlin写的attrs非空检查，别漏了initView()
        //初始化CSEditText
        initView()
        this.context = context
    }


    //监听器---------------------------------------------------------------------------------------
    //addListener(textWatcher)
    //实现TextWatcher接口匿名类 ，三个方法
    private val textWatcher = object : TextWatcher {

        var inputStart = -1
        var inputEnd = -1


        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            LogUtil.n(TAG, "beforeTextChanged: $s - $start - $count - $after")
        }

        /**
         * 获取文本更改的起始位置
         * @param s CharSequence
         * @param start Int 变化起始位置
         * @param before Int 变化起始位置被删除的的字符数（被删除或替换的字符数）
         * @param count Int 变化起始位置新增的字符数
         */
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inputStart = start
            inputEnd = start + count
            LogUtil.n(TAG, "onTextChanged: 文本监听器 $inputStart - $inputEnd")
        }

        /**
         * 通知s某地方已更改，需要对此修改进一步修改，可用此方法
         * * 注意：不要陷入死循环，自己所做的修改也会调用该函数，做好条件判断
         * * 要知道改变的跨度 start end，可使用 onTextChanged
         * @param s Editable - s.toString获取纯文本
         */
        override fun afterTextChanged(s: Editable?) {
            LogUtil.e(TAG, "afterTextChanged: ${s.toString()}")
            if (editable) {
                if (inputEnd > inputStart) {
                    for (tool in tools) {
                        tool.applyStyle(inputStart, inputEnd)
                    }
                } else {
                    for (tool in tools) {
                        tool.applyStyle(inputEnd, inputStart)
                    }
                }
            }
        }
    }

    //初始化编辑区 - initView
    private fun initView() {
        //this.focusable = View.FOCUSABLE_AUTO
        //当前视图可获得焦点
        this.isFocusable = true
        //前视图在触摸模式下也可获得焦点
        this.isFocusableInTouchMode = true
        //请求当前视图获取焦点
        this.requestFocus()
        //设置当前视图的背景色为透明
        setBackgroundColor(0)
        //普通文本，允许输入多行，并关闭输入建议
        this.inputType = (
                EditorInfo.TYPE_CLASS_TEXT //普通文本 设置此类型后，输入法将提供相应的键盘布局和输入建议。
                        or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE //附加标志，表示允许输入多行文本
                        or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS//附加标志，表示关闭输入建议
                )

        //设置其他 边距等
        //......
        this.setPadding(10)


        //添加监听器
        addTextChangedListener(textWatcher)
    }

    /**
     * 选中文本起始位置
     * * 例 0 'a' 1 'b' 2 'c'... 0,1,2都是位置 - 字符之间为位置
     * * 如 as 位置(1,1) - 即 a | s 之间
     * @param selStart Int
     * @param selEnd Int
     */
    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)

        LogUtil.e(TAG, "onSelectionChanged: 编辑区选中文本区间 ($selStart - $selEnd)")

        //选中文本根据工具项选中状态，设置span
        if (editable) {
            if (tools.isNotEmpty()) { //if (tools != null && tools.size() > 0)
                for (tool in tools) {
                    //保证a<b，有些输入法倒着选择，会导致a>b
                    if (selEnd >= selStart) {
                        tool.onSelectionChanged(selStart, selEnd)
                    } else {
                        tool.onSelectionChanged(selEnd, selStart)
                    }
                }
            }
        }

        // 自定义逻辑：更新工具栏按钮状态
        //toolbar.updateButtonStates(selStart, selEnd)

        // 自定义逻辑：记录选区变化
        //selectionHistory.add(SelectionRange(selStart, selEnd))
    }

    /**
     * 将 工具栏 与 CSEditText 关联
     * * 1 CSEditText.tools 接受 toolContainer工具栏的 CSToolItem
     * * 2 CSToolItem.setEditText(cs:CSEditText)
     * @param toolContainer CSToolContainer
     */
    fun setupWithToolContainer(toolContainer: CSToolContainer) {
        this.tools = toolContainer.getTools()
        for (tool in tools) {
            //CSToolItem.setEditText 传入CSEditText对象
            tool.setEditText(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //设计 clickToSwitchSpan
        return super.onTouchEvent(event)
    }

    fun setEditable(editable: Boolean) {
        this.editable = editable
        isEnabled = editable
        isFocusable = editable
    }


    //html保存加载--------------------------------------------------------------------------------------

    fun fromHtml(html: String) {
        fromHtml(html, 0)
    }

    fun fromHtml(html: String, textSizeOffset: Int) {
        val current = editable //保存状态
        editable = false //加载期间 样式不可应用

        val imageGetter = CSImageGetter(context, this)
        val tagHandler = CSTagHandler()
        val spanned: Spanned = CSHtml.fromHtml(
            html,
            CSHtml.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH,
            //标志，指示<p>元素中的文本在默认情况下将用一个换行符与其他文本分隔
            //imageGetter,
            imageGetter,
            tagHandler,
            textSizeOffset
        )
        if (spanned.isNotEmpty()) {
            (spanned as SpannableStringBuilder).delete(spanned.length - 1, spanned.length)
        }
        setText(spanned)


        /*val imageHGetterWM = WMImageGetter(context, this)
        val tagHandlerWM = WMTagHandler()
        val spannedWM: Spanned = WMHtml.fromHtml(
            html,
            CSHtml.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH,
            //标志，指示<p>元素中的文本在默认情况下将用一个换行符与其他文本分隔
            imageHGetterWM,
            tagHandlerWM,
            textSizeOffset
        )*/

        editable = current
    }

    /**
     * toHtml
     * @return String
     */
    fun getHtml(): String {
        LogUtil.e(TAG, "getHtml：文本长度 length = ${editableText.length}")
        val htmlStringBuilder = StringBuilder().apply {
            append("<html><body>")
            //将由'\n'分隔的每行文本包装在<p>或<li>元素中。这允许附加的段落样式在相应的<p>或<li>元素中编码为CSS样式
            append(CSHtml.toHtml(editableText, CSHtml.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL))
            append("</body></html>")
        }
        return htmlStringBuilder.toString()
    }

}