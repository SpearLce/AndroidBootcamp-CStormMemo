package com.illidancstormrage.csrich.toolitem

import android.content.Context
import android.graphics.Color
import android.text.style.AbsoluteSizeSpan
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.illidancstormrage.csrich.richeditor.CSHorizontalScrollView
import com.illidancstormrage.csrich.richeditor.otherui.CSButton
import com.illidancstormrage.csrich.utils.ColorUtil
import com.illidancstormrage.csrich.utils.ScreenUtil
import com.illidancstormrage.csrich.utils.cstool.StyleUtil
import com.illidancstormrage.utils.log.LogUtil

class CSToolTextSize : CSToolItem(), View.OnClickListener {

    companion object {
        private const val TAG = "CSToolTextSize"
    }

    init {
        _typeSpanClass = AbsoluteSizeSpan::class.java
    }

    //下弹出框
    private lateinit var popupWindow: PopupWindow

    //字体默认的大小 - 16
    var textSize: Int = 16


    /**
     * 情况1：afterTextChanged
     * 情况2：...
     * @param start Int
     * @param end Int
     */
    override fun applyStyle(start: Int, end: Int) {
        setStyle(start, end)
    }

    override fun setStyle(start: Int, end: Int) {
        StyleUtil.setStyle(this, start, end)
    }

    override fun removeStyle(start: Int, end: Int) {
        //设置字体大小不需要移除字体大小样式(字体肯定有大小)
        //可以将此函数包装成 重置字体大小，利用setStyle 和 默认 textSize
    }

    override fun getView(context: Context): List<View> {
        val button = CSButton(context)
        button.textSize = 19F
        button.text = textSize.toString()
        //includeFontPadding
        // - 去除字体内部的额外顶部和底部内边距（可能影响基线对齐）
        button.includeFontPadding = false

        view = button
        (view as CSButton).setOnClickListener {
            if (getEditText() == null) {
                return@setOnClickListener
            }

            popupWindow = PopupWindow(context)
            //给每个字体大小弄一个按钮，放到已经定义好的水平滚动视图
            val horizontalScrollView = CSHorizontalScrollView(context)
            //horizontalScrollView.isHorizontalScrollBarEnabled = true

            for (i in 12 until 30 step 2) {
                val buttonCharSize = CSButton(context)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                buttonCharSize.layoutParams = layoutParams
                buttonCharSize.gravity = Gravity.CENTER
                buttonCharSize.text = i.toString()
                //buttonCharSize.
                //按钮背景颜色
                buttonCharSize.setBackgroundColor(
                    //渐变
                    ColorUtil.calculateGradientColor(
                        Color.parseColor("#0ebeff"),
                        Color.parseColor("#ff42b3"),
                        i - 11,
                        18
                    )
                )
                if (textSize == i) {
                    //设置当前字体的按钮背景 对比起来
                    buttonCharSize.setBackgroundColor(Color.parseColor("#FFFF00"))
                    //buttonCharSize.setBackgroundResource(R.drawable.icon_circle) //button 圈颜色不好
                }
                buttonCharSize.id = i
                buttonCharSize.setOnClickListener(this)
                horizontalScrollView.addItemView(buttonCharSize)
            }


            //设置内容视图
            popupWindow.contentView = horizontalScrollView
            //设置高度
            popupWindow.height = ScreenUtil.getPixelByDp(context, 45F)
            //设置背景颜色 无效 / 被按钮样式覆盖 / 按钮被主题限制死
            //popupWindow.setBackgroundDrawable(ColorDrawable(0x8A2BE2));

            //设置外部可触摸关闭
            popupWindow.isOutsideTouchable = true;//用户在弹窗外区域点击时，弹窗会被关闭
            //定位并显示弹窗
            //-90 --》toolContainer h=90
            val offsetY: Int = ScreenUtil.getPixelByDp(context, -90F)
            /**
             * anchor：it - 锚点View，即弹窗相对于哪个View来显示
             * xoff - 对于锚点View的水平偏移量
             * yoff - 对于锚点View的垂直偏移量
             */
            popupWindow.showAsDropDown(it, 0, offsetY)
            //以锚点View it为基准，向下偏移90dp的位置显示出来


        }

        val views: MutableList<View> = ArrayList()
        views.add(view as CSButton)
        return views
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        StyleUtil.onSelectionChanged(this, selStart, selEnd)
    }


    override fun onCheckStateUpdate() {
        LogUtil.n(TAG, "pop按钮点击后 (view as CSButton).text前  = ${(view as CSButton).text}")
        (view as CSButton).text = textSize.toString()
        LogUtil.n(TAG, "pop按钮点击后 (view as CSButton).text后  = ${(view as CSButton).text}")
    }

    /**
     * 工具项popupWindow中
     * 字体大小按钮 - 被点击时候，设置大小
     * @param v View
     */
    override fun onClick(v: View?) {
        //LogUtil.e(TAG, "pop按钮的id = ${v!!.id}")
        setFontTextSize(v!!.id) //将id作为存储变量
        popupWindow.dismiss()
    }

    private fun setFontTextSize(textSize: Int) {
        this.textSize = textSize
        onCheckStateUpdate()

        getEditText()?.let { editText ->
            val selStart: Int = editText.selectionStart
            val selEnd: Int = editText.selectionEnd
            if (selStart < selEnd) {
                setStyle(selStart, selEnd)
            } else {
                setStyle(selEnd, selStart)
            }
        }
    }

}