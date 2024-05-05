package com.illidancstormrage.csrich.span

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.text.Layout
import android.text.style.QuoteSpan

//QuoteSpan 实现可以了，提供间距参数，还实现了ParcelableSpan序列化接口(传输用)
class CSQuoteSpan : QuoteSpan {

    /**
     * 条纹宽度
     */
    private var stripeWidth = STANDARD_STRIPE_WIDTH_PX

    /**
     * [[--|--]] 段落偏移间隙距离
     */
    private var gapWidth = STANDARD_GAP_WIDTH_PX

    private var color = STANDARD_COLOR

    /**
     * [[--]]|-- 绘制条纹前置偏移,offset<gapWidth
     */
    private var offset = 60

    companion object {
        private const val TAG = "CSQuoteSpan"

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }

    constructor() : this(color = -0x3b3730, stripeWidth = 5, gapWidth = 100, offset = 60) {
        this.gapWidth = 100
        this.stripeWidth = 5
        this.color = -0x3b3730
        this.offset = 60
    }

    constructor(color: Int) : super(color)

    /**
     *
     * @param color Int 条纹颜色
     * @param stripeWidth Int 条纹宽度
     * @param gapWidth Int 间隙宽度
     * @constructor
     */
    constructor(color: Int, stripeWidth: Int, gapWidth: Int, offset: Int) : super(
        color,
        stripeWidth,
        gapWidth
    ) {
        this.gapWidth = gapWidth
        this.color = color
        this.stripeWidth = stripeWidth
        this.offset = offset
    }

    constructor(src: Parcel) : super(src)


    /**
     * LeadingMarginSpan接口方法
     * @param first Boolean 是否为第一行
     * @return Int 整段偏移距离
     */
    override fun getLeadingMargin(first: Boolean): Int {
        return stripeWidth + gapWidth //quote引用线的距离 [--|------]xxx
    }

    /**
     * LeadingMarginSpan接口方法
     *  可以在偏移的位置里面进行各种效果绘制
     * @param c Canvas 绘图表面，用于在屏幕上绘制图形
     * @param p Paint 画笔对象，包含了绘制图形的颜色、样式、字体等属性
     * @param x Int 当前行起始边距的原点坐标
     * @param dir Int 指示文本方向的整数，正值表示从左到右，负值表示从右到左
     * @param top Int 当前行顶部相对于整个视图的垂直偏移
     * @param baseline Int 当前行基线相对于整个视图的垂直偏移
     * @param bottom Int 当前行底部相对于整个视图的垂直偏移
     * @param text CharSequence 当前行底部相对于整个视图的垂直偏移
     * * 传入的 start 和 end 由外部调用者在设置 span 时指定
     * @param start Int span 在 text 中的起始位置
     * @param end Int span 在 text 中的结束位置
     * @param first Boolean 同 getLeadingMargin，标识当前行是否为段落第一行
     * @param layout Layout 包含文本布局信息的对象，可以用来获取文本行的其他相关布局数据
     */
    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence, //可以投射为cast as SpannableStringBuilder
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout
    ) {
        //重写需要绘制竖线(实心方形实现)，否则为仅为LeadingMarginSpan，即tab表现
        val paint = Paint()
        paint.color = color
        //dir 有正负 - 文本方向
        c.drawRect(
            (x + offset).toFloat(), //x起点 - 左上角坐标(x1,y1)
            top.toFloat(),
            (x + offset + stripeWidth * dir).toFloat(),//右下角坐标(x2,y2)
            bottom.toFloat(),
            paint
        )
    }
}