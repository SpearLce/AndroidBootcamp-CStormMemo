package com.illidancstormrage.test.room.provider.data


import com.illidancstormrage.test.room.model.History
import com.illidancstormrage.test.room.model.MemoRecord

object DataUtil {
    /*val categoryList = arrayOf(
        "日常饮食", "工作安排", "会议纪要", "学习笔记",
        "健康追踪", "财务记录", "灵感创意", "旅行计划",
        "购物清单", "人脉管理", "项目进度", "家务待办",
        "阅读清单", "技能提升", "情感日记"
    )
    val audioList = arrayOf(
        Audio("content://1a", "xafasff", "这是我！不一样的烟火"),
        Audio("file://", "afgwegew   ", "梦中我痴痴牵挂"),
        Audio(null, "sagegeg21", "会议记录"),
        Audio("content://12://1a", "", "梦中我痴痴牵挂"),
        Audio(null, "", "会议记录")
    )*/
    val memoRecordList = arrayOf(
        MemoRecord("笔记1", "<span>内容1</span>", 1714433223, 1, 1),
        MemoRecord("笔记2", "<span>内容2</span>", 1714233423, 2, 2),
        MemoRecord("笔记3", "<span>内容3</span>", 1714413523, 3, 3),
        MemoRecord("笔记4", "<span>内容4</span>", 1714433923, 1, 2)
    )
    val historyList = arrayOf(
        History(1,"历史记录1",1714439999),
        History(2,"历2记录1",1714213423),
        History(1,"历史344记录1",1714433200),
        History(2,"历史记录111",1714233423),
        History(1,"历史记12录1",1714433000),
        History(3,"历史记331录1",1714410523)
    )
}