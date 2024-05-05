package com.illidancstormrage.csrich.richeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

open class CSHorizontalScrollView : HorizontalScrollView {

    companion object {
        private const val TAG = "CSHorizontalScrollView"
    }
    /*init {
        // 如果有自定义属性需要从 XML 中读取，可以在这里使用 TypedArray
    }*/

    private var context: Context? = null


    //线性布局
    //  horizontalScrollView继承FrameLayout，只能包含一个直接子类，所用添加布局器
    private var linearLayout: LinearLayout? = null


    constructor(context: Context) : super(context) {
        this.context = context
        initView()
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
        initView()
    }


    private fun initView() {
        linearLayout = LinearLayout(context)
        linearLayout!!.gravity = Gravity.CENTER_HORIZONTAL
        linearLayout!!.orientation = LinearLayout.HORIZONTAL
        this.apply {
            addView(
                linearLayout,
                LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            //水平滚动条是否已启用
            isHorizontalScrollBarEnabled = false
        }

    }


    /**
     * 绘制黑色边框
     * @param canvas Canvas
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //边框
        /*val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            // 可以定义一个 dimen 文件中的资源作为边框宽度
            //strokeWidth = resources.getDimension(R.dimen.border_width)
            //Stroke属性定义一条线，文本或元素轮廓颜色：
            strokeWidth = 10f
        }
        //创建矩形
        //创建一个新的空Rect。所有坐标都初始化为0。
        val bounds = RectF()
        bounds.set(0f, 0f, width.toFloat(), height.toFloat())
        canvas?.drawRoundRect(bounds, 20f, 20f, paint)*/
    }


    //添加工具项的view
    fun addItemView(view: View) {
        //直接在 linearLayout 中添加
        linearLayout!!.addView(view)
    }

}