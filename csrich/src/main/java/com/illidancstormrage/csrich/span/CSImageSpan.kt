package com.illidancstormrage.csrich.span

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.style.ImageSpan

/**
 * 主要是保存了 图片的资源路径（三种路径），重写 getSource
 * * 示例重写 draw
 * @property mContext Context?
 * @property mUri Uri?
 * @property mUrl String?
 * @property mResId Int
 */
class CSImageSpan : ImageSpan {


    enum class ImageType {
        URI,
        URL,
        RES
    }

    lateinit var drawable: BitmapDrawable


    private var mContext: Context? = null

    private var mUri: Uri? = null

    private var mUrl: String? = null

    private var mResId = 0

    //构造函数---------------------------------------

    //本地图片uri构造
    constructor(context: Context, bitmap: Bitmap, uri: Uri) : super(
        context,
        bitmap,
        ALIGN_CENTER //默认垂直居中
    ) {
        this.mContext = context
        this.mUri = uri
    }

    //传入网络图片url的构造
    constructor(context: Context, bitmap: Bitmap, url: String) : super(
        context,
        bitmap,
        ALIGN_CENTER
    ) {
        this.mContext = context
        this.mUrl = url
    }

    //res资源id
    constructor(context: Context, resId: Int) : super(context, resId, ALIGN_CENTER) {
        this.mContext = context
        this.mResId = resId
    }

    //drawable
    constructor(
        drawable: Drawable,//可绘制的对象
        // 如位图（Bitmap）、形状（Shape）、颜色块（ColorDrawable）等
        source: String //Drawable 的来源信息
    ) : super(drawable, source, ALIGN_CENTER)
    /*
    假设您正在创建一个 ImageSpan，并打算插入一张名为 my_image.png 的图片到文本中。
    这张图片位于应用的资源目录 res/drawable 下。那么：

    Drawable：您会通过 Resources.getDrawable() 方法或其他类似机制加载 my_image.png，
    得到一个 BitmapDrawable 对象。这个对象就是您提供的 Drawable 参数，
    它包含了图像的像素数据以及如何绘制这些像素到屏幕上的方法。

    source：在本例中，source 应该是 "@drawable/my_image"，这是一个资源 ID 字符串，
    表示 Drawable 来源于应用程序资源，并指定了具体的资源文件名。
    即使您没有直接持有原始的 Bitmap 数据，也能通过这个 source 信息重新加载或定位到相同的 Drawable。
     */

    constructor(context: Context, drawable: Drawable) : super(drawable) {
        this.mContext = context
    }


    //重写函数---------------------------------------

    //绘制示例
    override fun draw(
        canvas: Canvas, //绘制的目标画布
        text: CharSequence?,//包含 ImageSpan 的完整文本
        start: Int,//ImageSpan 在文本中的起始字符位置
        end: Int,//ImageSpan 在文本中的结束字符位置
        x: Float,//图片相对于文本基线的 X 坐标
        top: Int,//图片顶部距离所在行顶部的距离
        y: Int,//图片基线相对于所在行基线的偏移量（通常等于 ascent）
        bottom: Int,//图片底部距离所在行底部的距离
        paint: Paint//用于绘制文本的画笔，可能包含一些样式信息
    ) {
        super.draw(canvas, text, start, end, x, top, y, bottom, paint)
        //是 Android 中用于在文本中插入图片的一种 ReplacementSpan 实现
        //负责在指定的 Canvas 上绘制图片。
        /*
        //示例：
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight

        // 2. 计算图片在 canvas 上的绘制位置和大小
        val drawLeft = x
        val drawTop = top + (bottom - top - intrinsicHeight) / 2 // 居中垂直对齐
        val drawRight = drawLeft + intrinsicWidth
        val drawBottom = drawTop + intrinsicHeight

        // 3. 绘制图片到 canvas
        drawable.setBounds(drawLeft.toInt(), drawTop.toInt(), drawRight.toInt(), drawBottom.toInt())
        drawable.draw(canvas)*/
    }

    /**
     * Returns the source string that was saved during construction.
     * 返回在构造期间保存的源资源位置的字符串。
     */
    override fun getSource(): String? {
        return if (this.mUri != null) {
            this.mUri.toString() //mUri 保证给予 - 永久访问权限
        } else if (this.mUrl != null) {
            this.mUrl;          //mUrl
        } else {
            this.mResId.toString() //mResId
        }
    }
}