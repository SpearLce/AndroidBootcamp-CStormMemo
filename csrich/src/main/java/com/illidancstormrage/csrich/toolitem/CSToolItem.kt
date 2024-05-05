package com.illidancstormrage.csrich.toolitem

import android.content.Context
import android.graphics.Typeface
import android.view.View
import com.illidancstormrage.csrich.richeditor.CSEditText

//一个工具按钮的抽象类
abstract class CSToolItem {
    protected var view: View? = null

    private var editText: CSEditText? = null

    private var styleState = false

    //字符样式
    protected var _typefaceStyle: Int = Typeface.NORMAL

    val typefaceStyle: Int
        get() = _typefaceStyle

    //span Class
    protected lateinit var _typeSpanClass: Class<*>

    val typeSpanClass: Class<*>
        get() = _typeSpanClass


    //应用span样式 - 指定的文本范围（start 至 end 位置）
    abstract fun applyStyle(start: Int, end: Int)

    //set
    abstract fun setStyle(start: Int, end: Int)

    //移出spanStyle样式
    abstract fun removeStyle(start: Int, end: Int)


    //构建工具栏或菜单时，调用此方法获取与当前工具项相关的视图列表，用于布局和显示
    //将toolItem的view设置好，get出去
    abstract fun getView(context: Context): List<View>?

    //当文本编辑区的选区发生变化时（用户开始或结束选中文本），
    //此方法被调用，通知工具项新的选区范围（selStart 至 selEnd）。
    abstract fun onSelectionChanged(selStart: Int, selEnd: Int)

    //当工具项的样式状态（styleState）发生改变时，调用此方法通知子类更新与状态相关的行为或界面。
    abstract fun onCheckStateUpdate()


    //StyleState 样式启用状态 getter setter
    fun getStyleState(): Boolean {
        return styleState
    }

    fun setStyleStateAndUpdateUi(styleState: Boolean) {
        this.styleState = styleState
        onCheckStateUpdate()
        view?.invalidate()
    }

    //EditText对象 getter setter
    fun getEditText() = editText

    fun setEditText(editText: CSEditText) {
        this.editText = editText
    }

    fun getViewForUiStateUpdate() = this.view

}