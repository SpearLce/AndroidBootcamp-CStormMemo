package com.illidancstormrage.csrich.toolitem

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.PopupWindow
import com.illidancstormrage.csrich.R
import com.illidancstormrage.csrich.richeditor.CSHorizontalScrollView
import com.illidancstormrage.csrich.richeditor.otherui.CSImageButton
import com.illidancstormrage.csrich.utils.ScreenUtil

/**
 * 段落分割线 多种分割线
 * 不涉及Span
 */
class CSToolSplitLine : CSToolItem() {

    companion object {
        private const val TAG = "CSToolSplitLine"
    }

    private lateinit var popupWindow: PopupWindow

    override fun getView(context: Context): List<View>? {

        val imageButton = CSImageButton(context)
        imageButton.setImageResource(R.drawable.icon_text_splitline)
        view = imageButton
        (view as CSImageButton).setOnClickListener{
            if (getEditText() == null) {
                return@setOnClickListener
            }


            popupWindow = PopupWindow(context)
            val horizontalScrollView = CSHorizontalScrollView(context)



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


    override fun applyStyle(start: Int, end: Int) {
        TODO("Not yet implemented")
    }
    override fun setStyle(start: Int, end: Int) {
        TODO("Not yet implemented")
    }
    override fun removeStyle(start: Int, end: Int) {
        TODO("Not yet implemented")
    }
    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        TODO("Not yet implemented")
    }
    override fun onCheckStateUpdate() {
        TODO("Not yet implemented")
    }
}