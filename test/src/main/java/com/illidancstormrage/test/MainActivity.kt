package com.illidancstormrage.test

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import cn.hutool.core.date.DateUtil
import com.hjq.permissions.XXPermissions
import com.illidancstormrage.test.databinding.ActivityMainBinding
import com.illidancstormrage.test.room.dao.User
import com.illidancstormrage.test.room.database.CSMemoDatabase
import com.illidancstormrage.test.room.model.History
import com.illidancstormrage.test.room.provider.data.DataUtil
import com.illidancstormrage.test.untils.requestReadCalendar
import com.illidancstormrage.test.untils.requestWriteCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Random
import kotlin.concurrent.thread
import kotlin.random.asKotlinRandom

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.insert.setOnClickListener {
            XXPermissions.with(this)
                .requestWriteCalendar(this)
                .requestReadCalendar(this) {
                    addEventToCalendar(
                        this,
                        "测试事件",
                        "这是测试事件的描述",
                        Calendar.getInstance(),
                        getTomorrow()
                    )
                }
        }

    }

    fun testCalendar() {
        //流程：创建日历->根据日历id创建日程（事件）->创建日程（事件）的提醒时间


    }

    private fun getTomorrow(): Calendar {
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)
        return tomorrow
    }

    private fun addEventToCalendar(
        context: Context,
        title: String,
        description: String,
        startDate: Calendar,
        endDate: Calendar
    ) {
        val calendarUri = Uri.parse("content://com.android.calendar/events")

        val cr = context.contentResolver
        val values = ContentValues()

        // Event values
        values.put(CalendarContract.Events.CALENDAR_ID, getCalendarId(cr)) // 获取日历ID
        values.put(CalendarContract.Events.TITLE, title)
        values.put(CalendarContract.Events.DESCRIPTION, description)
        values.put(CalendarContract.Events.DTSTART, startDate.timeInMillis)
        values.put(CalendarContract.Events.DTEND, endDate.timeInMillis)
        values.put(CalendarContract.Events.ALL_DAY, 0) // 0 for false, 1 for true
        values.put(CalendarContract.Events.HAS_ALARM, 1) // 0 for false, 1 for true
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

        // Insert event
        val uri = cr.insert(calendarUri, values)

        // Get the event ID that is the last element in the Uri
        val eventID = uri!!.lastPathSegment?.toLong()

        // If you want to add a reminder
        if (eventID != null) {
            addReminderToEvent(cr, eventID, 60) // 60 minutes before the event
        }
    }

    @SuppressLint("Range")
    private fun getCalendarId(contentResolver: ContentResolver): Int {
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection =
            "${CalendarContract.Calendars.VISIBLE} = 1 AND ${CalendarContract.Calendars.IS_PRIMARY} = 1"
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        var calendarId: Int? = null
        if (cursor != null && cursor.moveToFirst()) {
            calendarId = cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            cursor.close()
        }

        return calendarId ?: -1
    }

    private fun addReminderToEvent(
        contentResolver: ContentResolver,
        eventId: Long,
        minutesBefore: Int
    ) {
        val reminderValues = ContentValues()
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId)
        reminderValues.put(CalendarContract.Reminders.MINUTES, minutesBefore)
        reminderValues.put(
            CalendarContract.Reminders.METHOD,
            CalendarContract.Reminders.METHOD_ALERT
        )

        val reminderUri =
            contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
    }

    fun testDb() {

        val db = CSMemoDatabase.getMemoryDatabase(this@MainActivity)
        lifecycleScope.launch(Dispatchers.IO) {

            val testDao = db.testDao()
            val memoDao = db.memoDao()
            val historyDao = db.historyDao()

            //准备数据(静态数据)
            for (memo in DataUtil.memoRecordList) {
                memoDao.insert(memo)
            }
            for (history in DataUtil.historyList) {
                historyDao.insert(history)
            }

            //测试插入多条history(关联外键)
            //静态 2 条
            val history = History(
                memoId = 4,
                historyContent = "123",
                editTime = 123144,
                id = 0
            )
            historyDao.insert(history)
            historyDao.insert(history)
            //动态

            findViewById<Button>(R.id.insert).setOnClickListener {
                thread {
                    val memoId = (Random().asKotlinRandom().nextInt(4) + 1).toLong()
                    val history2 = History(
                        memoId = if (memoId > 0L) memoId else 1L,
                        historyContent = "123",
                        editTime = DateUtil.date().time,
                        id = 0
                    )
                    historyDao.insert(history2)
                }
            }
        }
    }
}


//tag=:EditorFragment tag=:CSToolImage tag=:androidruntime  tag=:EditorViewModel tag=:test tag=:testInsert

