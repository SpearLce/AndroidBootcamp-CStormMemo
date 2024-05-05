package com.illidancstormrage.csrich.richeditor

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.setPadding
import com.illidancstormrage.csrich.toolitem.CSToolBold
import com.illidancstormrage.csrich.toolitem.CSToolImage
import com.illidancstormrage.csrich.toolitem.CSToolItalic
import com.illidancstormrage.csrich.toolitem.CSToolQuote
import com.illidancstormrage.csrich.toolitem.CSToolStrikethrough
import com.illidancstormrage.csrich.toolitem.CSToolTextColor
import com.illidancstormrage.csrich.toolitem.CSToolTextSize
import com.illidancstormrage.csrich.toolitem.CSToolUnderline
import com.illidancstormrage.csrich.utils.annotation.DevelopingDebug

class CSTextEditor : LinearLayout {


    //编辑区
    private lateinit var editText: CSEditText

    //工具栏
    private lateinit var toolContainer: CSToolContainer

    //工具项
    private val toolImage = CSToolImage()
    private val toolBold = CSToolBold()
    private val toolItalic = CSToolItalic()
    private val toolUnderline = CSToolUnderline()
    private val toolStrikethrough = CSToolStrikethrough()
    private val toolTextSize = CSToolTextSize()
    private val toolTextColor = CSToolTextColor()
    private val toolCSQuoteSpan = CSToolQuote()

    //view构造函数--------------------------------------------------------------------------------
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initView()
    }


    /**
     * 初始化编辑器View
     */
    private fun initView() {


        //1 滚动视图 - 编辑区
        val scrollView = ScrollView(context)
        //编辑区
        editText = CSEditText(context)
        scrollView.addView(
            editText,
            ViewGroup.LayoutParams( //scrollView的LayoutParams在基类ViewGroup中
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        //editor 设置 VERTICAL
        this.orientation = VERTICAL
        //scrollView添加到editor
        this.addView(
            scrollView,
            LayoutParams( //LayoutParams是this - LinearLayout的
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        ) //滚动编辑区 权重 剩余全部

        //debug
        //wrap - 45dp
        //2 工具栏
        toolContainer = CSToolContainer(context)
        val h = 90 //dp
        this.addView(
            toolContainer,
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, h) //ori
            //LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, h)
        )

        //工具栏初始化
        //3 初始化工具项
        toolContainer.addToolItem(toolImage)
        //3.1 一类模版
        toolContainer.addToolItem(toolBold)
        toolContainer.addToolItem(toolItalic)
        toolContainer.addToolItem(toolUnderline)
        toolContainer.addToolItem(toolStrikethrough)
        //3.2 展开下栏
        toolContainer.addToolItem(toolTextSize)
        toolContainer.addToolItem(toolTextColor)
        //3.3 段落
        toolContainer.addToolItem(toolCSQuoteSpan)
        editText.setupWithToolContainer(toolContainer) //编辑区持有工具项实例

    }

    /**
     * 工具项启动Intent，接受回调的数据
     * @param data Intent?
     */
    fun onActivityResult(data: Intent?) {
        //这里只需要处理回调返回图片的URI即可
        toolImage.onActivityResult(data)
    }

    //提供view一般功能函数----------------------------------------------------------------------------

    /**
     * 调整编辑器工具栏是否可见 - 是否启用富文本
     * @param editable Boolean
     * @return CSTextEditor
     */
    fun setEditable(editable: Boolean): CSTextEditor {
        editText.setEditable(editable)
        if (editable) {
            toolContainer.visibility = View.VISIBLE
        } else {
            toolContainer.visibility = View.GONE
        }
        return this //链式调用
    }

    fun setEditTextMaxLines(maxLines: Int): CSTextEditor {
        editText.maxLines = maxLines
        return this
    }

    fun setEditTextPadding(left: Int, top: Int, right: Int, bottom: Int): CSTextEditor {
        editText.setPadding(left, top, right, bottom)
        return this
    }

    fun setEditTextPadding(size: Int): CSTextEditor {
        editText.setPadding(size)
        return this
    }

    fun setEditTextLineSpacing(add: Float, mult: Float): CSTextEditor {
        editText.setLineSpacing(add, mult)
        return this
    }

    @DevelopingDebug("开发重置样式Item,现不支持重置光标样式，插入文本匹配目标源文本")
    fun insertTextAtEnd(textToInsert: String) {
        val editableText = editText.editableText
        // 获取并检查光标位置，确保其有效
        val cursorPosition = editText.selectionStart.coerceIn(0, editableText.length)
        //需要一键重置样式
        /*
        //此段不正确，使得光标锁死，span样式应用错误
        val spansToRemove = editableText.getSpans(cursorPosition, cursorPosition, Any::class.java)
        spansToRemove.forEach { span ->
            LogUtil.e("test", "span = $span")
            val spanStart = editableText.getSpanStart(span)
            val spanEnd = editableText.getSpanEnd(span)
            if (spanStart < cursorPosition) {
                editableText.setSpan(
                    span,
                    spanStart,
                    cursorPosition,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (spanEnd > cursorPosition) {
                editableText.setSpan(
                    span,
                    cursorPosition,
                    spanEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            editableText.removeSpan(span)
        }*/
        // 插入文本到光标位置
        editableText.insert(cursorPosition, textToInsert)
        // 保持光标在插入文本之后的位置
        editText.setSelection(cursorPosition + textToInsert.length)
    }


    fun isEmpty(): Boolean {
        return editText.text.toString().isEmpty()
    }

    //加载保存html---------------------------------------------------------------------------------
    fun fromHtml(html: String): CSTextEditor {
        editText.fromHtml(html)
        return this
    }

    fun fromHtml(html: String?, textSizeOffset: Int): CSTextEditor {
        editText.fromHtml(html!!, textSizeOffset)
        return this
    }

    fun getHtml(): String {
        return editText.getHtml()
    }

}