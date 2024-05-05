package com.illidancstormrage.cstormmemo

import cn.hutool.core.date.DateTime
import cn.hutool.core.date.DateUtil
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.Date

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Before
    fun printPre() = println("-".repeat(20 * 8))

    @After
    fun printAft() = printPre()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun timeStamp() {
        println(DateUtil.date().time.toString())
        val ts = DateUtil.date().time.toString().toLong()
        println(DateTime.of(ts.toLong()))
        println(DateUtil.date(DateTime.of(ts),))
        println(DateUtil.date(ts))
        println( DateUtil.date(ts).toString("yyyy年MM月dd日 HH时mm分ss秒"))
        println( DateUtil.date(ts).toString("yyyy年MM月dd日 HH时mm分"))
        println( DateUtil.date(ts).toString("M月d日H时"))
    }
}