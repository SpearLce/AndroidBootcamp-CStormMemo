package com.illidancstormrage.csrich.span

import android.text.style.LeadingMarginSpan
//import android.text.style.LeadingMarginSpan.LeadingMarginSpan2
//LeadingMarginSpan2 - getLeadingMarginLineCount - 可以控制影响的行数

/**
 * LeadingMarginSpan用来控制整个段落左边或者右边显示某些特定效果，里面有两个接口方法。
 * getLeadingMargin first为是否为第一行，返回值为整个段落偏移的距离
 * drawLeadingMargin 可以在偏移的位置里面进行各种效果绘制
 */
abstract class CSLeadingMarginSpan:LeadingMarginSpan {
}