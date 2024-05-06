package com.illidancstormrage.cstormmemo.utils.calendar

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.icu.util.Calendar //icu的支持国际化，java通用的
import android.icu.util.TimeZone
import android.net.Uri
import android.provider.CalendarContract

object CalendarUtil {

    //提醒时间
    var minutesBefore = 0

    fun addEventToCalendar(
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

        // Insert event 插入
        val uri = cr.insert(calendarUri, values)

        // Get the event ID that is the last element in the Uri
        // 获取Uri中最后一个元素的事件ID
        val eventID = uri!!.lastPathSegment?.toLong()

        // If you want to add a reminder
        if (eventID != null) {
            addReminderToEvent(cr, eventID, minutesBefore) // 60 minutes before the event
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
}