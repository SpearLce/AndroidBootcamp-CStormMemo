package com.illidancstormrage.csrich.utils.cstool

import android.text.Layout
import android.widget.TextView

object LeadingMarginUtil {
    /**
     * 为给定 Layout 对象中的一行计算 前面所有行的 前导偏移字符数，
     * 同时考虑 CharSequence 中的换行字符 ('\n')
     * @param layout Layout
     * @param text CharSequence
     * @param currentLine Int
     * @return Int 返回当前行(独立行)行开头的文本偏移量
     */
    fun getTextLead(layout: Layout, text: CharSequence, currentLine: Int): Int {
        var lead = 0 //前导偏移数
        var curLine = currentLine
        while (curLine > 0) { //确保不会超出文本的首行
            lead = layout.getLineStart(curLine) // 获取当前行的起始字符偏移量
            val char = text[lead - 1] //提取当前行偏移临近字符
            //**
            if (char == '\n') { //检查当前行开始前的一个字符是否为换行符
                break //是换行符，换行符所划分的实际上构成了独立的文本行，非内容过长转下一行显式的长行
            } else {
                lead = 0 //如果不是换行符，即当前行为超长行的溢出下一行显示的内容，
            }
            curLine-- //正在行开头在上一行
        }
        return lead
    }

    fun getTextLead(offset: Int, textView: TextView): Int {
        var lead = 0 //前导偏移数
        var curLine = getLineForOffset(textView, offset)
        val layout = textView.layout
        val text = textView.text
        while (curLine > 0) { //确保不会超出文本的首行
            lead = layout.getLineStart(curLine) // 获取当前行的起始字符偏移量
            val char = text[lead - 1] //提取当前行偏移临近字符
            //**
            if (char == '\n') { //检查当前行开始前的一个字符是否为换行符
                break //是换行符，换行符所划分的实际上构成了独立的文本行，非内容过长转下一行显式的长行
            } else {
                lead = 0 //如果不是换行符，即当前行为超长行的溢出下一行显示的内容，
            }
            curLine-- //正在行开头在上一行
        }
        return lead
    }


    /**
     * 同上，返回当前行行末尾 距离整段文本text开头的文本偏移量
     * @param layout Layout
     * @param text CharSequence
     * @param currentLine Int
     * @return Int
     */
    fun getTextEnd(layout: Layout, text: CharSequence, currentLine: Int): Int {
        var lead = text.length
        var curLine = currentLine
        while (curLine < layout.lineCount) {
            lead = layout.getLineEnd(curLine) //返回指定行最后一个字符 后的文本偏移量。
            if (text[lead - 1] == '\n') { // \n属于本行
                break
            }
            curLine++
        }
        return lead
    }

    fun getTextEnd(offset: Int, textView: TextView): Int {
        var end = 0 //前导偏移数
        var curLine = getLineForOffset(textView, offset)
        val layout = textView.layout
        val text = textView.text
        while (curLine < layout.lineCount) {
            end = layout.getLineEnd(curLine) //返回指定行最后一个字符 后的文本偏移量。
            if (text[end - 1] == '\n') { // \n属于本行
                break
            }
            curLine++
        }
        return end
    }

    /**
     * 行索引
     * @param layout Layout? 文本布局对象
     * @param offset Int 文本中的一个字符偏移量
     * @return Int 返回 offset 所对应的文本 行索引
     */
    private fun getLineForOffset(textView: TextView, offset: Int): Int {
        val layout = textView.layout
        return layout?.getLineForOffset(offset) ?: -1
    }
}