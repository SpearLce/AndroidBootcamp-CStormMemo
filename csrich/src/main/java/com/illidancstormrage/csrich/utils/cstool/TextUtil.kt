package com.illidancstormrage.csrich.utils.cstool

import android.widget.TextView

object TextUtil {
    /**
     * 获取当前文本位置偏移量 所在的行头 的文本偏移位置
     * @param offset Int 当前文本位置偏移量
     * @param textView TextView 继承自TextView的文本组件
     * @return Int 返回所在行的开始文本偏移量
     */
    @JvmStatic
    fun getTextLead(offset: Int, textView: TextView): Int {
        var lead = 0
        val layout = textView.layout
        //获取文本偏移量的行号 - 用于 getLineStart等
        var currentLine = layout.getLineForOffset(offset)
        val text = textView.text
        //确定以'\n'结尾行的开头 - 可能getLineForOffset获取的是内容过长溢出下一行的行号
        while (currentLine > 0) {
            lead = layout.getLineStart(currentLine)
            if (text[lead - 1] != '\n') { //行开头前一个字符不是\n
                //不是整个文本开头就是当前行为溢出行
                lead = 0
                currentLine--
            } else {
                break
            }
        }
        return lead
    }

    @JvmStatic
    fun getTextEnd(offset: Int, textView: TextView): Int {
        val text = textView.text
        val layout = textView.layout
        var currentLine = layout.getLineForOffset(offset)
        var end: Int = text.length //返回这个字符序列的长度
        while (currentLine < layout.lineCount) {
            end = layout.getLineEnd(currentLine) //返回指定行最后一个字符后的文本偏移量
            if (end > 0) {//越界判断
                if (text[end - 1] == '\n') {
                    break
                }
            }
            currentLine++
        }
        return end
    }

}