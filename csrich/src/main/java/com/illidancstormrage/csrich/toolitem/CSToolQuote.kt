package com.illidancstormrage.csrich.toolitem

import android.content.Context
import android.text.Spanned
import android.text.style.ParagraphStyle
import android.view.View
import android.widget.TextView
import com.illidancstormrage.csrich.R
import com.illidancstormrage.csrich.richeditor.otherui.CSImageButton
import com.illidancstormrage.csrich.span.CSQuoteSpan
import com.illidancstormrage.csrich.utils.cstool.TextUtil
import com.illidancstormrage.csrich.utils.cstool.WMUtil
import com.illidancstormrage.utils.log.LogUtil

/**
 * 有bug，
 * 1 当前行length为0，没有零宽空格就无法显示样式
 * 2 当前行应用样式后，再在该行输入新字符，新字符无法使用该样式
 */
class CSToolQuote : CSToolItem() {

    companion object {
        private const val TAG = "CSToolQuote"
    }

    /**
     * editText中afterTextChanged是当前输入的字符也应用该样式
     * * 应用 引用样式
     * @param start Int
     * @param end Int
     */
    override fun applyStyle(start: Int, end: Int) {
        LogUtil.e("DebugUtil", "applyStyle触发 ---- 开始")

        val editable = this.getEditText()!!.editableText


        //情况1： 避免在新的一行开始处重复应用样式
        //  检查用户是否在新的一行开始处（即前一个字符是换行符 \n）进行输入
        //  如果是，则直接返回，不执行后续样式应用操作
        //  这是因为通常情况下，新行起始处可能不需要应用特定样式，或者某些样式（如引用标记）不应跨行重复出现。
        //  start 大于 0（即不是文本开头/防止越界）
        if (start > 0 && editable[start - 1] == '\n') {
            LogUtil.e(
                "DebugUtil",
                "applyStyle触发 ---- 回车后不应用该样式 | editable[start - 1] = ${editable[start - 1]}"
            )
            return //回车新行：不进行后续连续样式应用
        }



        //查找在当前输入位置前一个字符处是否存在 CSQuoteSpan 样式（可能是用于表示引用的特殊样式）
        val styles =
            //开头前一个字符
            editable.getSpans(start - 1, start, CSQuoteSpan::class.java)
        LogUtil.e("DebugUtil", "styles[] = $styles | length = ${styles.size}")

        if (styles != null && styles.isNotEmpty()) {
            //如果存在这样的样式标记，说明用户正在对已经应用了该样式的文本进行编辑，那么接下来可能需要对样式进行调整或保持连续性
            val style = styles[styles.size - 1]
            val spanStart = editable.getSpanStart(style)//Span开始
            val spanEnd = editable.getSpanEnd(style)    //Span结束
            LogUtil.e("DebugUtil", "spanStart = ${editable.getSpanStart(style)} ")
            LogUtil.e("DebugUtil", "spanEnd = ${editable.getSpanEnd(style)} ")


            //如果 start 至 end 范围内的文本内容仅包含一个换行符（\n）
            //情况1：
            if (editable.subSequence(start, end).toString() == "\n") {
                LogUtil.e("DebugUtil", "仅输入回车")
                /*if (spanEnd == end - 1) { //end - 1：除添加的\n
                    LogUtil.e("DebugUtil", "(spanEnd == end - 1) -> $spanEnd,${end - 1}")
                    if (spanEnd == spanStart + 1) {
                        LogUtil.e(
                            "DebugUtil",
                            "(spanEnd == spanStart + 1) -> $spanEnd,${spanStart + 1}"
                        )
                        //editable.delete(end - 2, end)
                    } else {
                        //editable.insert(end, "\u200B")
                        editable.setSpan(
                            CSQuoteSpan(),
                            end,
                            end, //end + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }*/
            }


        }

    }

    override fun getView(context: Context): List<View> {
        CSQuoteSpan.context = context

        val imageButton = CSImageButton(context)

        imageButton.setImageResource(R.drawable.icon_text_quote)
        view = imageButton
        (view as CSImageButton).setOnClickListener {
            if (null == getEditText()) {
                return@setOnClickListener
            }
            val editable = getEditText()!!.editableText
            val selStart = getEditText()!!.selectionStart
            val selEnd = getEditText()!!.selectionEnd

            //选中所有行开头
            val textLead: Int = TextUtil.getTextLead(selStart, getEditText() as TextView)
            //选中所有行结尾
            val textEnd: Int = TextUtil.getTextEnd(selEnd, getEditText() as TextView)

            //查找选中行所有引用span
            val spans = editable.getSpans(textLead, textEnd, CSQuoteSpan::class.java)

            /*
            * 条件1：
            * 行数等引用数，每句都使用引用样式 --> 此时点击==删除
            * */
            //1 前置：开头 ！= 结尾
            //2 有 - 引用span（如果是第一次那么会转else）
            //3 引用数 == 段落数  （即 行数 等于 应用引用数）
            if (spans != null && textEnd != textLead && spans.size == WMUtil.getParagraphCount(
                    getEditText(),
                    selStart,
                    selEnd
                )
            ) {
                for (span in spans) {
                    editable.removeSpan(span)
                }
            }
            /*
            * 条件2：
            * 行数不等引用数 --> 此时给选中的所有行添加引用
            * */
            else {
                var startLine = textLead
                while (startLine <= textEnd) {
                    val endLine = TextUtil.getTextEnd(startLine, getEditText()!!)
                    //获取当前行的Quote样式,无则添加，有则不用管
                    val currentLineSpans =
                        editable.getSpans(startLine, endLine, CSQuoteSpan::class.java)
                    //如果当前行[startLine,endLine]没有Quote样式就添加
                    if (currentLineSpans.isEmpty() || startLine == endLine || currentLineSpans == null) {
                        //添加步骤
                        // 1 清除其他段落样式，补全牵连非选中行的样式
                        val paragraphStyles =
                            editable.getSpans(startLine, endLine, ParagraphStyle::class.java)
                        for (paragraphStyle in paragraphStyles) {
                            val startSpan = editable.getSpanStart(paragraphStyles)
                            val startEnd = editable.getSpanEnd(paragraphStyles)
                            //1 清除
                            editable.removeSpan(paragraphStyle)
                            //2 补全
                            if (startSpan < startLine) {
                                editable.setSpan(
                                    paragraphStyle,
                                    startSpan,
                                    startLine,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                            if (endLine < startEnd) {
                                editable.setSpan(
                                    paragraphStyle,
                                    endLine,
                                    startEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                        // 2 添加Quote样式
                        //editable.insert(startLine, "\u200B")  //不行
                        editable.setSpan(
                            CSQuoteSpan(),
                            startLine,
                            endLine,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        //editable.delete(startLine, startLine + 1)
                        //例外：当前行没有文本(length=0),添加段落样式无效,不显示，所以要添加[零宽度空格 \u200B]

                    }
                    startLine = endLine //换新行
                    if (startLine == textEnd) break //循环选中所有行末尾
                }
            }
        }

        val views: MutableList<View> = ArrayList()
        views.add(view as CSImageButton)
        return views
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
    }

    override fun setStyle(start: Int, end: Int) {
        TODO("Not yet implemented")
    }

    override fun removeStyle(start: Int, end: Int) {
        TODO("Not yet implemented")
    }

    override fun onCheckStateUpdate() {
        TODO("Not yet implemented")
    }

}