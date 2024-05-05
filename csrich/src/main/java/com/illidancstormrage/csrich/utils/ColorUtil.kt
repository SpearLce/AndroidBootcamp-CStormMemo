package com.illidancstormrage.csrich.utils


import android.graphics.Color as GraphicsColor //取别名，否则与内置枚举类冲突

object ColorUtil {

    enum class Color(val rgbValue: Int, val rgbName: String) {
        TRANSPARENT(0, "透明"),
        BLACK(0xFF333333.toInt(), "雅黑色"),
        GARY(0xFFC0C0C0.toInt(), "灰色"),
        CA8A8A8(0xFFA8A8A8.toInt(), "浅灰色"),
        RED(0xFFFF2400.toInt(), "橙红色"),
        ORANGE(0xFFFF7F00.toInt(), "橙色"),
        YELLOW(0xFFFFFF00.toInt(), "黄色"),
        C99CC32(0xFF99CC32.toInt(), "黄绿色"),
        C238E23(0xFF238E23.toInt(), "森林绿"),
        GREEN(0xFF238E68.toInt(), "海绿色"),
        CYAN(0xFF00FFFF.toInt(), "青色"),
        CC0D9D9(0xFFC0D9D9.toInt(), "浅蓝色"),
        BLUE(0xFF3299CC.toInt(), "天蓝色"),
        C3232CD(0xFF3232CD.toInt(), "中蓝色"),
        CE47833(0xFFE47833.toInt(), "桔黄色"),
        BROWN(0xFFA67D3D.toInt(), "棕色"),
        CFC9D99(0xFFFC9D99.toInt(), "粉色"),
        CFF1CAE(0xFFFF1CAE.toInt(), "艳粉红色"),
        DB70DBC(0xFFDB70DB.toInt(), "淡紫色"),
        C9932CD(0xFF9932CD.toInt(), "深兰花色");
    }


    /**
     * 两个颜色之间渐变
     * @param startColor Int
     * @param endColor Int
     * @param step Int
     * @return Int
     */
    fun calculateGradientColor(startColor: Int, endColor: Int, step: Int, totalSteps: Int): Int {
        //Gradient = A + (B-A) * N / Step

        // Ensure the input values are valid
        require(step in 0 until totalSteps) {
            "Step must be between 0 and $totalSteps (inclusive)"
        }
        //#0ebeff
        //#ff42b3

        // Extract RGB components from start and end colors
        val startRed = GraphicsColor.red(startColor)
        val startGreen = GraphicsColor.green(startColor)
        val startBlue = GraphicsColor.blue(startColor)
        val startAlpha = GraphicsColor.alpha(startColor)

        val endRed = GraphicsColor.red(endColor)
        val endGreen = GraphicsColor.green(endColor)
        val endBlue = GraphicsColor.blue(endColor)
        val endAlpha = GraphicsColor.alpha(endColor)

        // Perform linear interpolation for each color component
        val gradientRed = startRed + ((endRed - startRed) * step.toDouble() / totalSteps).toInt()
        val gradientGreen =
            startGreen + ((endGreen - startGreen) * step.toDouble() / totalSteps).toInt()
        val gradientBlue =
            startBlue + ((endBlue - startBlue) * step.toDouble() / totalSteps).toInt()
        val gradientAlpha =
            startAlpha + ((endAlpha - startAlpha) * step.toDouble() / totalSteps).toInt()

        // Combine the interpolated color components into a single color value
        return GraphicsColor.argb(gradientAlpha, gradientRed, gradientGreen, gradientBlue)
    }
}