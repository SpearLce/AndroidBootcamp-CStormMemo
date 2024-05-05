package com.illidancstormrage.csrich.richeditor

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.illidancstormrage.csrich.toolitem.CSToolItem

class CSToolContainer : CSHorizontalScrollView {
    private var context: Context? = null

    //工具栏 - 工具项集合
    private var tools = ArrayList<CSToolItem>()

    constructor(context: Context) : super(context) {
        this.context = context
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
        this.context = context
    }


    //添加工具项view到
    fun addToolItem(toolItem: CSToolItem) {
        // 1 ToolItem保存到list
        //   添加工具项到集合中保存
        tools.add(toolItem)

        // 2 ToolItem添加到视图中
        //   一个item，可能是几个工具按钮组成 List
        //   获取工具项viewList,并添加到linearLayout中
        val views = toolItem.getView(context!!)
        if (views != null) {
            for (view in views) {
                //设置背景透明
                view.setBackgroundColor(0)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                )
                //外边距在LayoutParams设置
                val lr = 20 //设置工具栏中工具项左右间距
                layoutParams.setMargins(lr,0,lr,0)
                view.layoutParams = layoutParams
                //内边距在view设置
                view.setPadding(1) //设置大了，字体显示不正常
                //添加到CSHorizontalScrollView的linearLayout中
                //linearLayout.addItemView
                this.addItemView(view)
            }
        }
    }

    fun getTools(): List<CSToolItem> = tools


}