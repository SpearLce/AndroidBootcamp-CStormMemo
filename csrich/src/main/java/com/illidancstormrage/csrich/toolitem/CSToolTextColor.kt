package com.illidancstormrage.csrich.toolitem

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.illidancstormrage.csrich.R
import com.illidancstormrage.csrich.richeditor.CSHorizontalScrollView
import com.illidancstormrage.csrich.richeditor.otherui.CSImageButton
import com.illidancstormrage.csrich.utils.ColorUtil
import com.illidancstormrage.csrich.utils.ScreenUtil
import com.illidancstormrage.csrich.utils.cstool.StyleUtil

class CSToolTextColor : CSToolItem(), View.OnClickListener {

    companion object {
        private const val TAG = "CSToolTextColor"
    }

    var textColor = ColorUtil.Color.BLACK.rgbValue
    private lateinit var popupWindow: PopupWindow


    init {
        _typeSpanClass = ForegroundColorSpan::class.java
    }


    override fun applyStyle(start: Int, end: Int) {
        setStyle(start, end)
    }

    override fun setStyle(start: Int, end: Int) {
        StyleUtil.setStyle(this, start, end)
    }

    override fun removeStyle(start: Int, end: Int) {
    }

    override fun getView(context: Context): List<View>? {
        val imageButton = CSImageButton(context)
        imageButton.setImageResource(R.drawable.icon_text_textcolor)
        view = imageButton
        (view as CSImageButton).setOnClickListener {

            if (getEditText() == null) {
                return@setOnClickListener
            }


            popupWindow = PopupWindow(context)
            val horizontalScrollView = CSHorizontalScrollView(context)


            for (type in ColorUtil.Color.entries) {
                if (type.rgbValue == 0) //取出透明
                    continue
                val imageColorBtn = CSImageButton(context)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                val margin = ScreenUtil.getPixelByDp(context, 10F)
                //layoutParams.setMargins(margin / 2, margin / 2, margin / 2, margin)
                layoutParams.setMargins(margin / 3, margin, margin / 3, margin)
                val padding = ScreenUtil.getPixelByDp(context, 5F)
                //imageColorBtn.setPadding(padding)
                imageColorBtn.layoutParams = layoutParams
                imageColorBtn.setBackgroundColor(type.rgbValue)
                if (type.rgbValue == textColor) {
                    imageColorBtn.setImageResource(R.drawable.icon_selected)
                }
                imageColorBtn.id = type.rgbValue
                imageColorBtn.setOnClickListener(this)
                horizontalScrollView.addItemView(imageColorBtn)
            }

            popupWindow.contentView = horizontalScrollView
            popupWindow.height = ScreenUtil.getPixelByDp(context, 45F)
            popupWindow.setBackgroundDrawable(ColorDrawable(0x50FFFF))
            popupWindow.isOutsideTouchable = true;
            val offsetY: Int = ScreenUtil.getPixelByDp(context, -90F)
            popupWindow.showAsDropDown(it, 0, offsetY)

        }
        val views: MutableList<View> = ArrayList()
        views.add(view!!)
        return views
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        StyleUtil.onSelectionChanged(this, selStart, selEnd)
    }

    override fun onCheckStateUpdate() {
        (view as CSImageButton).setColorFilter(textColor)
        (view as CSImageButton).invalidate() //设置颜色，需要刷新一下draw，字不需要
    }

    override fun onClick(v: View?) {
        setFontTextColor(v!!.id)
        popupWindow.dismiss()
    }

    private fun setFontTextColor(textColor: Int) {
        this.textColor = textColor
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