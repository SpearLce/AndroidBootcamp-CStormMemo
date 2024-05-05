package com.illidancstormrage.csrich.richeditor.otherui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton


class CSImageButton : AppCompatImageButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec) //让成正方形
    }

}