package com.illidancstormrage.csrich.utils.cstool

import android.graphics.Color
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import com.illidancstormrage.csrich.toolitem.CSToolItem
import com.illidancstormrage.csrich.toolitem.CSToolTextColor
import com.illidancstormrage.csrich.toolitem.CSToolTextSize
import com.illidancstormrage.utils.log.LogUtil

object StyleUtil {

    fun applyStyle(toolItem: CSToolItem, start: Int, end: Int) {
        if (toolItem.getStyleState()) {
            setStyle(toolItem, start, end)
        } else {
            removeStyle(toolItem, start, end)
        }
    }

    fun setStyle(toolItem: CSToolItem, start: Int, end: Int) {

        //1 定义 sF, eF -> 处理style区间
        //确定style的区间 startFact，endFact
        //获取传入的设置区间，并初始化sF, eF
        // -- 下面if逻辑等，是得F区间要囊括传入的区间
        // -- 即 startFact<=startSpan(s)<endSpan(s)<=endFact
        var startFact = start
        var endFact = end


        LogUtil.e(
            toolItem.javaClass.simpleName,
            "测试setStyle1：设置样式更新前 - start,end = $start,$end"
        )


        //2 准备编辑区编辑内容实例 editable
        //-- getEditText 工具项持有editText实例
        val editable = toolItem.getEditText()?.editableText


        //3 查找已有样式集合
        //StyleSpan 类型表示文本样式的跨度
        val styleSpans = editable?.getSpans(
            start - 1, end + 1,
            toolItem.typeSpanClass
        )
        // -1, +1 选中区间前后字符牵连的span，更新sf，ef；这是是前后加粗样式相同
        // 也可以不用-1，+1，此项目未见影响
        //val styleSpans = editable?.getSpans(start, end, StyleSpan::class.java)


        LogUtil.e(
            toolItem.javaClass.simpleName,
            "测试setStyle2：styleSpans[start - 1, end + 1] = ${styleSpans.toString()}"
        )




        if (styleSpans != null) {
            //3.1 循环所有StyleSpan
            //功能：
            //更新 startFact, //实际开始
            //    endFact,   //实际结束

            for ((index, styleSpan) in styleSpans.withIndex()) {

                val spanStart = editable.getSpanStart(styleSpan)
                val spanEnd = editable.getSpanEnd(styleSpan)
                LogUtil.e(
                    toolItem.javaClass.simpleName,
                    "测试setStyle3：styleSpans不为空，循环区间styleSpans - " +
                            "[$index] spanStart = $spanStart " +
                            "[$index] spanEnd = $spanEnd"
                )
                when (styleSpan) {
                    //3.2 更新 sf / ef
                    //StyleSpan有粗体 斜体等
                    is StyleSpan -> {
                        // 当styleSpan 是当前样式(如粗体等) ，进行更新 sf，ef
                        if (styleSpan.style == toolItem.typefaceStyle) {
                            if (spanStart != spanEnd) {
                                //如果 spanStart 小于 start，则更新 start_fact 为 spanStart，
                                //即扩大样式范围的起始点（如:扩大加粗范围起点）
                                if (spanStart < start) {
                                    startFact = spanStart
                                }
                                //如果 spanEnd 大于 end，则更新 end_fact 为 spanEnd，即扩大样式范围的结束点。
                                if (spanEnd > end) {
                                    endFact = spanEnd
                                }

                                //逻辑：要设置的当前样式，如果该样式没有应用到选中的全部字符，那么就将其删除，以免样式重叠
                                //- 如果当前样式跨度完全包含指定范围（spanStart <= start && spanEnd >= end），
                                //- 说明当前区间已应用样式，无需进一步处理，直接返回。
                                if (spanStart <= start && spanEnd >= end) {
                                    return
                                } else {//否则，将该样式跨度从文本中移除（s.removeSpan(styleSpan)），以避免重叠或冲突的样式。
                                    editable.removeSpan(styleSpan)
                                }
                            }
                        }

                    }

                    is UnderlineSpan,
                    is StrikethroughSpan -> {

                        LogUtil.n(
                            toolItem.javaClass.simpleName,
                            "测试setStyle4：setStyle is UnderlineSpan"
                        )



                        if (spanStart != spanEnd) {
                            if (spanStart < start) {
                                startFact = spanStart
                            }
                            if (spanEnd > end) {
                                endFact = spanEnd
                            }

                            //要设置的当前样式，如果该样式没有应用到选中的全部字符，那么就将其删除，以免样式重叠
                            if (spanStart <= start && spanEnd >= end) {
                                return
                            } else {
                                editable.removeSpan(styleSpan)
                            }
                        }
                    }

                    //兼顾补全区间前后被修改样式（以下span仅有设置状态，没有取消状态）
                    is AbsoluteSizeSpan -> {
                        //1 处理已存在的相同字体大小样式
                        //若当前样式 span 的字体大小与目标大小（textSize）相等
                        if (styleSpan.size == (toolItem as CSToolTextSize).textSize) {
                            if (spanStart != spanEnd) {
                                //更新 startFact 和 endFact 以包含整个已有相同字体大小的区域（有部分区域不在原指定范围内）
                                if (spanStart < start) {
                                    startFact = spanStart
                                }
                                if (spanEnd > end) {
                                    endFact = spanEnd
                                }
                                //若整个已有区域完全覆盖原指定区域，则无需进一步操作，直接返回

                                if (spanStart <= start && spanEnd >= end) {
                                    return //有一个字体大小Span的起始位置囊括了整个区间 - 即就一个字体大小span
                                } else { //否则，从文本中移除该样式 span ==>
                                    // 如果不是区间都是已经设置为textSize，就移出其中为textSize的Spans
                                    editable.removeSpan(styleSpan) //防止样式重叠
                                }
                            }
                        } else {
                            //2 处理已存在的不同字体大小样式
                            //若当前样式 span 的字体大小与目标大小不同 且 其覆盖部分与指定区域重叠：
                            // - 即选中区域非目标textSize的字体大小Span，要删除，并补全选中区间前后牵连的字符的字体大小

                            if (spanEnd <= start || spanStart >= end) {
                                //不在选中区域的 - 如：[spanStart,spanEnd] [start,end]
                                //不用管
                            } else {
                                //与选取重叠的其他字体大小Span
                                // - 删除
                                editable.removeSpan(styleSpan)
                                // - 补全
                                if (spanStart < start) {
                                    editable.setSpan(
                                        AbsoluteSizeSpan(styleSpan.size, true),
                                        spanStart,
                                        start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                                if (spanEnd > end) {
                                    editable.setSpan(
                                        AbsoluteSizeSpan(styleSpan.size, true),
                                        end,
                                        spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                        }
                    }

                    is ForegroundColorSpan -> {
                        // ？ foregroundColor.toLong()
                        if (styleSpan.foregroundColor == (toolItem as CSToolTextColor).textColor) {
                            if (spanStart != spanEnd) {
                                if (spanStart < start) {
                                    startFact = spanStart
                                }
                                if (spanEnd > end) {
                                    endFact = spanEnd
                                }
                                if (spanStart <= start && spanEnd >= end) {
                                    return
                                } else {
                                    editable.removeSpan(styleSpan)
                                }
                            }
                        } else {
                            if (spanEnd <= start || spanStart >= end) {
                                // [spanStart,spanEnd] - [start,end] - [spanStart,spanEnd]
                            } else {
                                editable.removeSpan(styleSpan)
                                if (spanStart < start) {
                                    editable.setSpan(
                                        ForegroundColorSpan(
                                            toolItem.textColor
                                        ),
                                        startFact,
                                        endFact,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                                if (spanEnd > end) {
                                    editable.setSpan(
                                        ForegroundColorSpan(
                                            toolItem.textColor
                                        ),
                                        startFact,
                                        endFact,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        LogUtil.e(
                            toolItem.javaClass.simpleName,
                            "else setStyle -styleSpan Name ${styleSpan::class.java.simpleName}"
                        )
                    }
                }
            }
        }

        when (toolItem.typeSpanClass) {
            StyleSpan::class.java -> {
                //4 区间[startFact,endFact]设置样式
                editable?.setSpan(
                    StyleSpan(toolItem.typefaceStyle),
                    startFact, //实际开始
                    endFact,   //实际结束
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            //注意这里是 == 效果 ，项目中使用CSUnderlineSpan继承UnderlineSpan
            //而 is UnderlineSpan 是子类或自身
            UnderlineSpan::class.java,
            //CSUnderlineSpan::class.java //设置的是 UnderlineSpan.Class
            -> {
                LogUtil.n(
                    toolItem.javaClass.simpleName,
                    "setStyle-setSpan is UnderlineSpan::class.java"
                )

                editable?.setSpan(
                    //CSUnderlineSpan(),
                    UnderlineSpan(),
                    startFact, //实际开始
                    endFact,   //实际结束
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            StrikethroughSpan::class.java -> {

                editable?.setSpan(
                    StrikethroughSpan(),
                    startFact, //实际开始
                    endFact,   //实际结束
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            AbsoluteSizeSpan::class.java -> {
                editable?.setSpan(
                    //true：dp
                    //false：px
                    AbsoluteSizeSpan((toolItem as CSToolTextSize).textSize, true),
                    startFact,
                    endFact, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            ForegroundColorSpan::class.java -> {
                editable?.setSpan(
                    ForegroundColorSpan(
                        (toolItem as CSToolTextColor).textColor
                    ),
                    startFact,
                    endFact,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            else -> {
                LogUtil.e(
                    toolItem.javaClass.simpleName,
                    "setStyle-setSpan class = ${toolItem.typeSpanClass.simpleName}"
                )
            }
        }

        LogUtil.e(
            toolItem.javaClass.simpleName,
            "setStyle设置样式更新后 - startFact,endFact = $startFact,$endFact"
        )

    }

    //-------------------------------------------------------------------

    fun removeStyle(toolItem: CSToolItem, start: Int, end: Int) {
        //1 获取要移出区间的所有styleSpan(用于循环)
        val editable = toolItem.getEditText()?.editableText
        val styleSpans = editable?.getSpans(start, end, toolItem.typeSpanClass)

        //2 循环所有styleSpan，查找指定样式，并移出
        if (styleSpans != null) {
            for (styleSpan in styleSpans) {

                val spanStart: Int = editable.getSpanStart(styleSpan)
                val spanEnd: Int = editable.getSpanEnd(styleSpan)

                when (styleSpan) {
                    is StyleSpan -> {
                        if (styleSpan.style == toolItem.typefaceStyle) {
                            if (spanStart != spanEnd) {//移出span区域不为空
                                //移出样式逻辑：
                                // 1
                                // 只适合删除样式区域大于选中区域，如果选中区域一块一块的，
                                // 要先按工具项，设置区间全部应用样式，再移出样式
                                // 2
                                // 补全区间前后牵连的字符的样式
                                if (spanStart <= start && spanEnd >= end) {
                                    editable.removeSpan(styleSpan)
                                    //补上缺失 *<a>b* 中的 a
                                    //spanStart,start
                                    editable.setSpan(
                                        StyleSpan(toolItem.typefaceStyle),
                                        spanStart,
                                        start,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    //补上缺失
                                    //spanEnd
                                    editable.setSpan(
                                        StyleSpan(toolItem.typefaceStyle),
                                        end,
                                        spanEnd,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                        }
                    }

                    is UnderlineSpan -> {
                        if (spanStart != spanEnd) {
                            if (spanStart <= start && spanEnd >= end) {
                                editable.removeSpan(styleSpan)
                                //补上缺失 *<a>b* 中的 a
                                //spanStart,start
                                editable.setSpan(
                                    //CSUnderlineSpan(),
                                    UnderlineSpan(),
                                    spanStart,
                                    start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                //补上缺失
                                //spanEnd
                                editable.setSpan(
                                    //CSUnderlineSpan(),
                                    UnderlineSpan(),
                                    end,
                                    spanEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                    }

                    is StrikethroughSpan -> {
                        if (spanStart != spanEnd) {
                            if (spanStart <= start && spanEnd >= end) {
                                editable.removeSpan(styleSpan)
                                //补上缺失 *<a>b* 中的 a
                                //spanStart,start
                                editable.setSpan(
                                    StrikethroughSpan(),
                                    spanStart,
                                    start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                //补上缺失
                                //spanEnd
                                editable.setSpan(
                                    StrikethroughSpan(),
                                    end,
                                    spanEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun onSelectionChanged(toolItem: CSToolItem, selStart: Int, selEnd: Int) {
        LogUtil.e(
            toolItem.javaClass.simpleName,
            "样式响应文本选中区间 - onSelectionChanged [selStart,selEnd] = [$selStart,$selEnd]"
        )

        if (null == toolItem.getEditText()) {
            return
        }

        //1 更新工具项布尔标志 styleFlag
        //styleFlag 更新工具项UI和state
        var flag = false
        //获取编辑区两个实例
        val editText = toolItem.getEditText()
        val editable = editText?.editableText

        /**
         * 两种情况，以粗体为例
         * 1 光标闪烁模式，且光标前为粗体，则更新UI设置为true，在后面输入
         *      (输入字符触发在 EditText 中的监听器 afterTextChanged)的字加粗
         * 2 选中的全文，判断第一个字符的span区间是否大于等于选中区间
         *      (即，全选是否全加粗，全加粗使工具项为选中true状态)
         *
         * 拓展：点击工具项逻辑在getView的setOnClickListener中
         */
        // 情况1：光标模式
        // 如: [2,2]
        if (selStart > 0 && selStart == selEnd) {

            /**
             * styleSpans 获取光标前一个字符牵连的styleSpans
             * 判断是否有特定的样式
             */
            val styleSpans =
            //（selStart - 1）：检查紧邻当前选区起点（selStart）前一个字符是否存在特定的样式（如加粗样式）
                // 获取前一个字符一连串的样式 如点击[5,5],前五个字符都是加粗 - (样式区间：spanStart = 0 spanEnd = 5)
                editable?.getSpans(selStart - 1, selStart, toolItem.typeSpanClass)

            LogUtil.e(
                toolItem.javaClass.simpleName,
                "测试0：光标模式 - 前一个字符styleSpans = ${styleSpans.toString()}"
            )



            if (styleSpans != null) {

                LogUtil.e(
                    toolItem.javaClass.simpleName,
                    "测试0.1 - 循环前：光标模式 - styleSpans != null 成立"
                )


                //循环前置字符的所有styleSpan
                for ((index, styleSpan) in styleSpans.withIndex()) {


                    LogUtil.e(
                        toolItem.javaClass.simpleName,
                        "测试0.2：光标模式 - styleSpans 循环体"
                    )

                    val spanStart = editable.getSpanStart(styleSpan)
                    val spanEnd = editable.getSpanEnd(styleSpan)

                    LogUtil.e(
                        toolItem.javaClass.simpleName,
                        "光标模式前styleSpans - " +
                                "[$index] spanStart = $spanStart " +
                                "[$index] spanEnd = $spanEnd"
                    )

                    when (styleSpan) {
                        is StyleSpan -> {
                            if (styleSpan.style == toolItem.typefaceStyle) {

                                //粗体区长度非0，不是空的
                                if (spanStart != spanEnd) {
                                    //这是在检查当前 StyleSpan 对象所应用的文本范围是否具有非零长度。
                                    //两个if：
                                    //1 存在该工具项样式
                                    //2 且样式span非0，即存在
                                    flag = true
                                    //如果出现特殊样式错误，参考CSToolBold中下面函数
                                    //在特定条件下将光标及后一个字符设置为样式
                                    //applyConsistentBoldAroundCursor(editable, selStart, selEnd, styleSpan)
                                }
                            }
                        }

                        is UnderlineSpan,
                        is StrikethroughSpan -> {
                            if (spanStart != spanEnd) {
                                //两个if：
                                //1 存在该工具项样式
                                //2 且样式span非0，即存在
                                flag = true
                                //applyConsistentBoldAroundCursor(editable, selStart, selEnd, styleSpan)
                            }


                            LogUtil.e(
                                toolItem.javaClass.simpleName,
                                "测试0.2.1：光标模式 - styleSpans 循环体 的 when (styleSpan)"
                            )
                        }


                        //AbsoluteSpan
                        is AbsoluteSizeSpan -> {
                            //此处逻辑跟上面粗斜等逻辑一样，上面状态仅两种用布尔，字体类似一种状态，设置了字体大小，只不过数值在变
                            if (spanStart != spanEnd) {
                                (toolItem as CSToolTextSize).textSize = styleSpan.size
                            }
                        }

                        is ForegroundColorSpan -> {
                            if (spanStart != spanEnd) {
                                (toolItem as CSToolTextColor).textColor = styleSpan.foregroundColor
                            }
                        }
                    }

                }

                LogUtil.n(
                    toolItem.javaClass.simpleName,
                    "测试0.1 - 循环后：光标模式 x"
                )


            }

        }
        // 情况2：选中一段文本
        else if (selEnd != selStart) {

            val styleSpans =
            //只要选从第一个字符获取样式，
            //如果选中文本全部应用了该样式，
            //那么第一个字符获取的StyleSpan的作用区间是大于等于选中区间
                //也可以（selStart，selStart + 1）
                editable?.getSpans(selStart, selStart, toolItem.typeSpanClass)

            if (styleSpans != null) {
                for ((index, styleSpan) in styleSpans.withIndex()) {

                    val spanStart = editable.getSpanStart(styleSpan)
                    val spanEnd = editable.getSpanEnd(styleSpan)
                    LogUtil.n(
                        toolItem.javaClass.simpleName,
                        "选中文本模式第一个字符牵连styleSpans - " +
                                "[$index] spanStart = $spanStart " +
                                "[$index] spanEnd = $spanEnd"
                    )

                    when (styleSpan) {
                        is StyleSpan -> {
                            if (styleSpan.style == toolItem.typefaceStyle) {
                                if (spanStart <= selStart && spanEnd >= selEnd) {
                                    if (spanStart != spanEnd) {
                                        //同情况1
                                        //if 选中区间全部应用样式，且样式存在非0
                                        flag = true
                                    }
                                }
                            }
                        }

                        is UnderlineSpan,
                        is StrikethroughSpan -> {
                            if (spanStart <= selStart && spanEnd >= selEnd) {
                                if (spanStart != spanEnd) {
                                    //同情况1
                                    //if 选中区间全部应用样式，且样式存在非0
                                    flag = true
                                }
                            }
                        }

                        //参考代码写的是
                        //getSpans(selStart, selEnd, AbsoluteSizeSpan.class);
                        //这里还是采用选中区间的第一个字符牵连的span，
                        //跟粗体等逻辑一致，只有选中区域所有字符都应用了同一样式，才会触发工具项的属性样式更新
                        is AbsoluteSizeSpan -> {
                            if (spanStart <= selStart && spanEnd >= selEnd) {
                                if (spanStart != spanEnd) {
                                    //同情况1
                                    //if 选中区间全部应用样式，且样式存在非0
                                    (toolItem as CSToolTextSize).textSize = styleSpan.size
                                }
                            }
                        }

                        is ForegroundColorSpan -> {
                            if (spanStart <= selStart && spanEnd >= selEnd) {
                                if (spanStart != spanEnd) {
                                    (toolItem as CSToolTextColor).textColor =
                                        styleSpan.foregroundColor
                                }
                            }
                        }
                    }

                }
            }


        }

        LogUtil.n(
            toolItem.javaClass.simpleName,
            "测试1：toolItem.typeSpanClass = ${toolItem.typeSpanClass.simpleName}"
        )

        when (toolItem.typeSpanClass) {
            StyleSpan::class.java,
            //CSUnderlineSpan::class.java, //不使用
            UnderlineSpan::class.java,//确定使用这个 CSHtml中使用的只是UnderlineSpan
            StrikethroughSpan::class.java -> {
                toolItem.setStyleStateAndUpdateUi(flag)

                LogUtil.e(
                    toolItem.javaClass.simpleName,
                    "测试2：toolItem.typeSpanClass = ${toolItem.typeSpanClass.simpleName} 且 flag = $flag"
                )
            }

            AbsoluteSizeSpan::class.java -> {
                (toolItem as CSToolTextSize).onCheckStateUpdate()
            }

            ForegroundColorSpan::class.java -> {
                (toolItem as CSToolTextColor).onCheckStateUpdate()
            }

            else -> {
                LogUtil.e(
                    toolItem.javaClass.simpleName,
                    "onSelectionChanged class = ${toolItem.typeSpanClass.simpleName}"
                )
                LogUtil.e(
                    toolItem.javaClass.simpleName,
                    "测试3：toolItem.typeSpanClass = ${toolItem.typeSpanClass.simpleName}"
                )
            }
        }

    }

    fun onCheckStateUpdate(toolItem: CSToolItem) {

        toolItem.getViewForUiStateUpdate()?.let {
            if (toolItem.getStyleState()) {
                it.setBackgroundColor(Color.GRAY)
            } else {
                it.setBackgroundColor(0)
            }
        }
    }

    fun toolItemOnClickListener(toolItem: CSToolItem) {
        toolItem.apply {
            if (getEditText() == null) {
                return
            }
            //1 获取editText实例
            val editText = getEditText()
            //1.1 主动获取选中区间
            //    有被动通过editText的监听器方法 - onSelectionChanged
            val selStart = editText?.selectionStart
            val selEnd = editText?.selectionEnd

            //2 正确区间 / 判断工具项选中状态
            if (selStart != null && selEnd != null) {
                if (selStart < selEnd) { //保证起点<终点
                    if (getStyleState()) { //点击前是真
                        removeStyle(this, selStart, selEnd)
                    } else { //假
                        setStyle(this, selStart, selEnd)
                    }
                } else {
                    if (getStyleState()) { //点击前是真
                        removeStyle(this, selEnd, selStart)
                    } else { //假
                        setStyle(this, selEnd, selStart)
                    }
                }
            }
            //3 点击完之后翻转工具栏状态 / 并且更新状态与UI
            setStyleStateAndUpdateUi(!getStyleState())
        }
    }


}

