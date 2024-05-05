package com.illidancstormrage.csrich.toolitem

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.View
import com.illidancstormrage.csrich.R
import com.illidancstormrage.csrich.richeditor.otherui.CSImageButton
import com.illidancstormrage.csrich.utils.cstool.StyleUtil

class CSToolItalic : CSToolItem() {

    companion object {
        private const val TAG = "CStoolItalic"
    }

    init {
        _typefaceStyle = Typeface.ITALIC
        _typeSpanClass = StyleSpan::class.java
    }


    override fun applyStyle(start: Int, end: Int) {
        StyleUtil.applyStyle(this, start, end)
    }

    override fun setStyle(start: Int, end: Int) {
        StyleUtil.setStyle(this, start, end)
    }

    override fun removeStyle(start: Int, end: Int) {
        StyleUtil.removeStyle(this, start, end)
    }

    override fun getView(context: Context): List<View> {
        val imageButton = CSImageButton(context)
        imageButton.setImageResource(R.drawable.icon_text_italic)
        view = imageButton
        (view as CSImageButton).setOnClickListener {
            StyleUtil.toolItemOnClickListener(this@CSToolItalic)
        }
        val views: MutableList<View> = ArrayList()
        views.add(view!!)
        return views
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        StyleUtil.onSelectionChanged(this, selStart, selEnd)
    }

    override fun onCheckStateUpdate() {
        StyleUtil.onCheckStateUpdate(this)
    }
}