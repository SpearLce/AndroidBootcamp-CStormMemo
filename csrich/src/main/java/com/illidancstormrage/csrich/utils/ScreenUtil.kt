package com.illidancstormrage.csrich.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager


object ScreenUtil {
    @JvmStatic
    fun getPixelByDp(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension( //sdk工具类方法
            TypedValue.COMPLEX_UNIT_DIP, //输入单位为设备独立像素（dp）
            dpValue,//解析的要转换的dpValue
            context.resources.displayMetrics//提供了当前设备的显示度量信息，包括屏幕密度、宽度、高度等
        ).toInt()
    }

    @JvmStatic
    fun scaleBitmapToFitWidth(bitmap: Bitmap, maxWidth: Int): Bitmap? {
        val w = bitmap.width
        val h = bitmap.height
        val newHeight = maxWidth * h / w
        val matrix = Matrix()
        val scaleWidth = maxWidth.toFloat() / w
        val scaleHeight = newHeight.toFloat() / h
        if (w < maxWidth * 0.2) {
            return bitmap
        }
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
    }

    @JvmStatic
    fun getScreenWidthAndHeight(context: Context): IntArray? {
        val outSize = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        display.getSize(outSize)
        val widthAndHeight = IntArray(2)
        widthAndHeight[0] = outSize.x
        widthAndHeight[1] = outSize.y
        return widthAndHeight
    }
}