package com.illidancstormrage.csrich.toolitem

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import com.illidancstormrage.csrich.R
import com.illidancstormrage.csrich.richeditor.otherui.CSImageButton
import com.illidancstormrage.utils.log.LogUtil

class CSToolBold : CSToolItem() {

    companion object {
        const val TAG = "CSToolBold"
    }

    init {
        _typefaceStyle = Typeface.BOLD
        _typeSpanClass = StyleSpan::class.java
    }

    /**
     * start end 是editText的onSelectionChanged传过来的
     * @param start Int
     * @param end Int
     */
    override fun applyStyle(start: Int, end: Int) {
        if (getStyleState()) {
            setStyle(start, end)
        } else {
            removeStyle(start, end)
        }
    }
    /**
     * 需要区间 / 编辑区实例
     * @param start Int
     * @param end Int
     */
    override fun setStyle(start: Int, end: Int) {

        //确定粗体的区间 startFact，endFact
        //1 获取传入的设置区间
        //1.1 来源applyStyle
        var startFact = start
        var endFact = end

        //2 getEditText 工具项持有editText实例
        val editable = getEditText()?.editableText

        //查找并处理已有加粗样式
        //3 StyleSpan 类型表示文本样式的跨度
        LogUtil.e(TAG, "setStyle- 设置粗体 - ,前 tart,end = $start,$end")

         val styleSpans = editable?.getSpans(start - 1, end + 1, StyleSpan::class.java)
        //val styleSpans = editable?.getSpans(start, end, StyleSpan::class.java)
        if (styleSpans != null) {
            for (styleSpan in styleSpans) {
                //检查当前 StyleSpan 是否为加粗样式
                //更新 startFact, //实际开始
                //    endFact,   //实际结束

                LogUtil.e(
                    TAG, "setStyle- 设置粗体 - , spanStart = ${editable.getSpanStart(styleSpan)} " +
                            "spanEnd = ${editable.getSpanEnd(styleSpan)}"
                )

                if (styleSpan.style == Typeface.BOLD) {
                    //yes - 获取该样式跨度的起始位置 spanStart 和结束位置 spanEnd
                    val spanStart = editable.getSpanStart(styleSpan)
                    val spanEnd = editable.getSpanEnd(styleSpan)
                    if (spanStart != spanEnd) {
                        //如果 spanStart 小于 start，则更新 start_fact 为 spanStart，即扩大加粗范围的起始点
                        if (spanStart < start) {
                            startFact = spanStart
                        }
                        //如果 spanEnd 大于 end，则更新 end_fact 为 spanEnd，即扩大加粗范围的结束点。
                        if (spanEnd > end) {
                            endFact = spanEnd
                        }
                        //如果当前样式跨度完全包含指定范围（spanStart <= start && spanEnd >= end），说明已加粗，无需进一步处理，直接返回。
                        if (spanStart <= start && spanEnd >= end) {
                            return
                        } else {//否则，将该样式跨度从文本中移除（s.removeSpan(styleSpan)），以避免重叠或冲突的样式。
                            editable.removeSpan(styleSpan)
                        }
                    }
                }
            }
        }

        LogUtil.e(TAG, "setStyle- 设置粗体 - ,后 startFact,endFact = $startFact,$endFact")
        editable?.setSpan(
            StyleSpan(Typeface.BOLD),
            startFact, //实际开始
            endFact,   //实际结束
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    override fun removeStyle(start: Int, end: Int) {
        val editable = getEditText()?.editableText
        val styleSpans = editable?.getSpans(start, end, StyleSpan::class.java)

        if (styleSpans != null) {
            for (styleSpan in styleSpans) {
                //粗体
                if (styleSpan.style == Typeface.BOLD) {
                    //获取粗体span起始
                    val spanStart: Int = editable.getSpanStart(styleSpan)
                    val spanEnd: Int = editable.getSpanEnd(styleSpan)

                    //只适合删除粗体区域长于选中区域，如果选中区域一块一块的，要先按粗体，设置为全粗体，再删除
                    if (spanStart != spanEnd) {//移出span区域不为空
                        if (spanStart <= start && spanEnd >= end) {
                            editable.removeSpan(styleSpan)
                            //补上缺失 *<a>b* 中的 a
                            editable.setSpan(
                                StyleSpan(Typeface.BOLD),
                                spanStart,
                                start,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            //补上缺失
                            editable.setSpan(
                                StyleSpan(Typeface.BOLD),
                                end,
                                spanEnd,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                }
            }
        }
    }


    override fun getView(context: Context): List<View> {

        //view
        val imageButton = CSImageButton(context)
        imageButton.setImageResource(R.drawable.icon_text_bold)
        view = imageButton
        //view.setOnClickListener
        (view as CSImageButton).setOnClickListener {
            if (getEditText() == null) {
                LogUtil.e(TAG, "getView中没有获取到EditText实例")
                return@setOnClickListener
            }
            //1 获取editText实例
            val editText = getEditText()
            //val editable = editText?.editableText
            //1.1
            val selStart = editText?.selectionStart
            val selEnd = editText?.selectionEnd
            //LogUtil.e(TAG, "点击B：选中区间 [selStart,selEnd] = [$selStart,$selEnd] ")

            //2 正确区间 / 判断工具项选中状态
            if (selStart!! < selEnd!!) {
                if (getStyleState()) { //真
                    removeStyle(selStart, selEnd)
                } else { //假
                    setStyle(selStart, selEnd)
                }
            } else {
                if (getStyleState()) { //真
                    removeStyle(selEnd, selStart)
                } else { //假
                    setStyle(selEnd, selStart)
                }
            }
            //3 点击完之后翻转工具栏状态 / 并且更新状态与UI
            setStyleStateAndUpdateUi(!getStyleState())
        }

        val views: MutableList<View> = ArrayList()
        views.add(view!!)
        return views
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (null == getEditText()) {
            return
        }
        LogUtil.e(TAG, "加粗的 onSelectionChanged,[s,e] = [$selStart,$selEnd]")

        var boldFlag = false
        //获取文本区 2个常量
        val editText = this.getEditText()
        val editable = editText?.editableText

        /**
         * 1 光标前的粗体，更新UI设置为true，给后面的插入(在 EditText 的afterTextChanged)的字加粗
         * 2 选中的全文，判断第一个字符的span区间是否大于等于选中区间
         */

        // 如果当前文本仅光标闪烁，即选中0文本，此时点击粗体之类，可以为后续生成的字就自带粗体
        if (selStart > 0 && selStart == selEnd) {

            /**
             * styleSpans 前一个字符 selStart - 1, selStart
             */


            val styleSpans =
            //（selStart - 1）：检查紧邻当前选区起点（selStart）前一个字符是否存在特定的样式（如加粗样式）
                //获取前一个字符一连串的样式 如点击[5,5],前五个字符都是加粗 - (样式区间：spanStart = 0 spanEnd = 5)
                editable?.getSpans(selStart - 1, selStart, StyleSpan::class.java)

            if (styleSpans != null) {
                //循环前置字符的所有styleSpan
                for (styleSpan in styleSpans) {

                    LogUtil.e(
                        TAG,
                        "仅点击 - styleSpan区间, spanStart = ${editable.getSpanStart(styleSpan)} " +
                                "spanEnd = ${editable.getSpanEnd(styleSpan)}"
                    )

                    if (styleSpan.style == Typeface.BOLD) {


                        //粗体区长度非0，不是空的
                        if (editable.getSpanStart(styleSpan) != editable.getSpanEnd(styleSpan)) {
                            //这是在检查当前 StyleSpan 对象所应用的文本范围是否具有非零长度。

                            boldFlag = true //如果是粗体，那么状态设置true


                            //在特定条件下将光标及其临近字符设置为加粗样式
                            //applyConsistentBoldAroundCursor(editable, selStart, selEnd, styleSpan)


                        }
                    }
                }
            }

        } else if (selEnd != selStart) { //确保当前处于选区模式，即用户已选中一段文本
            val styleSpans =
            //只要选从第一个字符获取样式，应该如果选中文本全部应用了加粗，
                //那么第一个字符获取的StyleSpan的作用区间是大于等于选中区间
                editable?.getSpans(selStart, selStart, StyleSpan::class.java)

            if (styleSpans != null) {
                for (styleSpan in styleSpans) {

                    LogUtil.e(
                        TAG, "选中一段文本, spanStart = ${editable.getSpanStart(styleSpan)} " +
                                "spanEnd = ${editable.getSpanEnd(styleSpan)}"
                    )

                    if (styleSpan.style == Typeface.BOLD) {
                        if (editable.getSpanStart(styleSpan) <= selStart
                            && editable.getSpanEnd(styleSpan) >= selEnd
                        ) {
                            if (editable.getSpanStart(styleSpan) != editable.getSpanEnd(styleSpan)) {
                                boldFlag = true
                            }
                        }
                    }
                }
            }
        }

        setStyleStateAndUpdateUi(boldFlag)
    }


    private fun applyConsistentBoldAroundCursor(
        editable: Editable,
        selStart: Int,
        selEnd: Int,
        styleSpan: StyleSpan
    ) {
        val styleSpansNext =
            //光标后一个字符的样式，如果没有样式，跟这个也无关
            editable.getSpans(selStart, selStart + 1, StyleSpan::class.java)

        for (styleSpanNext in styleSpansNext) {

            LogUtil.e(
                TAG,
                "仅点击 - styleSpanNext区间 - , spanStart = ${
                    editable.getSpanStart(styleSpanNext)
                } " +
                        "spanEnd = ${editable.getSpanEnd(styleSpanNext)}"
            )

            if (styleSpanNext.style == Typeface.BOLD) {
                if (editable.getSpanStart(styleSpanNext) != editable.getSpanEnd(
                        styleSpanNext
                    )
                ) {
                    if (styleSpanNext != styleSpan) {
                        LogUtil.e(
                            TAG,
                            "styleSpanNext != styleSpan ?? - , styleSpanNext区间 ：spanStart = ${
                                editable.getSpanStart(styleSpanNext)
                            } " +
                                    "spanEnd = ${
                                        editable.getSpanEnd(
                                            styleSpanNext
                                        )
                                    } " +
                                    "-- [selStart-1,selStart+1]= [${selStart - 1},${selStart + 1}]"
                        )

                        setStyle(selStart - 1, selStart + 1)

                    }
                }
            }
        }
    }


    override fun onCheckStateUpdate() {
        if (getStyleState()) {
            view!!.setBackgroundColor(Color.GRAY)
        } else {
            view!!.setBackgroundColor(0)
        }
    }
}


