package com.shnu.androidoscanlendar;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Author: ShenDanLai on 2016/10/9.
 * Email: 17721129316@163.com
 */

public class CalendarDemo2 extends Activity {
    EditText et_calendar_id;
    //Android2.2版本以后的URL，之前的就不写了
    private static String calanderURL = "content://com.android.calendar/calendars";
    private static String calanderEventURL = "content://com.android.calendar/events";
    private static String calanderRemiderURL = "content://com.android.calendar/reminders";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);
        et_calendar_id = (EditText) findViewById(R.id.et_calendar_id);

    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onClick(View v) {
        if (v.getId() == R.id.readUserButton) {  //读取系统日历账户，如果为0的话先添加
            Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);

            System.out.println("Count: " + userCursor.getCount());
            Toast.makeText(this, "Count: " + userCursor.getCount(), Toast.LENGTH_LONG).show();
            for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
                System.out.println("name: " + userCursor.getString(userCursor.getColumnIndex("ACCOUNT_NAME")));
                String userName1 = userCursor.getString(userCursor.getColumnIndex("name"));
                String userName0 = userCursor.getString(userCursor.getColumnIndex("account_name"));
                String userNameId = userCursor.getString(userCursor.getColumnIndex("_id"));

                Toast.makeText(this, "NAME: " + userName1 + " -- ACCOUNT_NAME: " + userName0 + "ID:" + userNameId, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.inputaccount) {        //添加日历账户
            initCalendars();
        } else if (v.getId() == R.id.delEventButton) {  //删除事件

            int rownum = getContentResolver().delete(Uri.parse(calanderURL), "_id!=-1", null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
            //可以令_id=你添加账户的id，以此删除你添加的账户
            Toast.makeText(CalendarDemo2.this, "删除了: " + rownum, Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.readEventButton) {  //读取事件
            Cursor eventCursor = getContentResolver().query(Uri.parse(calanderEventURL), null, null, null, null);
            if (eventCursor.getCount() > 0) {
                eventCursor.moveToLast();             //注意：这里与添加事件时的账户相对应，都是向最后一个账户添加
                String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                String CalendarID = eventCursor.getString(eventCursor.getColumnIndex("calendar_id"));//CalendarContract.EventsColumns.CALENDAR_ID
                String eventID = eventCursor.getString(eventCursor.getColumnIndex("_id"));
                Toast.makeText(CalendarDemo2.this, CalendarID + "::::" + eventTitle, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.writeEventButton) {
            // 获取要出入的gmail账户的id
            String calId = "";
            Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);
            if (userCursor.getCount() > 0) {
                userCursor.moveToLast();  //注意：是向最后一个账户添加，开发者可以根据需要改变添加事件 的账户
                calId = userCursor.getString(userCursor.getColumnIndex("_id"));
            } else {
                Toast.makeText(this, "没有账户，请先添加账户", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues event = new ContentValues();
            event.put("title", "与苍井空小姐动作交流");
            event.put("description", "Frankie受空姐邀请,今天晚上10点以后将在Sheraton动作交流.lol~");
            // 插入账户
            event.put("calendar_id", calId);
            System.out.println("calId: " + calId);
            event.put("eventLocation", "地球-华夏");

            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.HOUR_OF_DAY, 11);
            mCalendar.set(Calendar.MINUTE, 45);
            long start = mCalendar.getTime().getTime();
            mCalendar.set(Calendar.HOUR_OF_DAY, 12);
            long end = mCalendar.getTime().getTime();
            event.put("dtstart", start);
            event.put("dtend", end);
            event.put("hasAlarm", 1);
            event.put("rrule","FREQ=WEEKLY;COUNT=10;WKST=SU;BYDAY=FR");
            event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");  //这个是时区，必须有，
            //添加事件
            Uri newEvent = getContentResolver().insert(Uri.parse(calanderEventURL), event);
            //事件提醒的设定
            long id = Long.parseLong(newEvent.getLastPathSegment());
            ContentValues values = new ContentValues();
            values.put("event_id", id);
            // 提前10分钟有提醒
            values.put("minutes", 10);

            getContentResolver().insert(Uri.parse(calanderRemiderURL), values);
            Toast.makeText(CalendarDemo2.this, "插入事件成功!!!", Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.updateSecondButton) {
            updateSecond();
        } else if (v.getId() == R.id.queryByIdButton) {
            String calendarId = et_calendar_id.getText().toString();
            queryByCalendarID(calendarId);
        }
    }


    //添加账户
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initCalendars() {

        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, "yy");

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com");
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.exchange");
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "mytt");
        value.put(CalendarContract.Calendars.VISIBLE, 1);
//        value.put(CalendarContract.Calendars.CALENDAR_COLOR, -9206951);  //-9206951 转换为十六进制转738395,这个颜色值就是颜色值
        /**
         * 如果时间颜色要为红色，可以使用 FF0000 减去16进制 1000000  得到的数值再转化为10进制为-65536
         */
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, -65536);  //-65536 为红色
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 3);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, "mygmailaddress@gmail.com");
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.exchange")
                .build();

        getContentResolver().insert(calendarUri, value);
    }

    //修改_id为2的日历账号颜色
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void updateSecond() {

        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, "yy");

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com");
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.exchange");
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "mytt123");
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, -9206951);  //-9206951 转换为十六进制转738395,这个颜色值就是颜色值
        /**
         * 如果时间颜色要为红色，可以使用 FF0000 减去16进制 1000000  得到的数值再转化为10进制为-65536
         */
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, "mygmailaddress@gmail.com");
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.exchange")
                .build();

        if (getContentResolver().update(calendarUri, value, "_id = ?", new String[]{"2"}) == 1) {
            Toast.makeText(CalendarDemo2.this, "修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CalendarDemo2.this, "修改失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查询指定calendar_id的所有事件
     */
    public void queryByCalendarID(String calendarId) {
        if (calendarId == null || calendarId.length() == 0) {
            Toast.makeText(this, "请先填写时间ID", Toast.LENGTH_SHORT).show();

        }
        Cursor eventCursor = getContentResolver().query(Uri.parse(calanderEventURL), null, "calendar_id = ?", new String[]{calendarId}, null);
        if (eventCursor.getCount() > 0) {
            while(eventCursor.moveToNext()){
//                eventCursor.moveToLast();             //注意：这里与添加事件时的账户相对应，都是向最后一个账户添加
                String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                String eventdescription = eventCursor.getString(eventCursor.getColumnIndex("description"));
                String eventeventLocation= eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
                String eventeventColor= eventCursor.getString(eventCursor.getColumnIndex("eventColor"));
                String eventeventColor_index= eventCursor.getString(eventCursor.getColumnIndex("eventColor_index"));
                String eventdisplayColor= eventCursor.getString(eventCursor.getColumnIndex("displayColor"));
                String eventeventStatus = eventCursor.getString(eventCursor.getColumnIndex("eventStatus"));
                String eventselfAttendeeStatus = eventCursor.getString(eventCursor.getColumnIndex("selfAttendeeStatus"));
                String eventsync_data1 = eventCursor.getString(eventCursor.getColumnIndex("sync_data1"));
                String eventsync_data2 = eventCursor.getString(eventCursor.getColumnIndex("sync_data2"));
                String eventsync_data3 = eventCursor.getString(eventCursor.getColumnIndex("sync_data3"));
                String eventsync_data4 = eventCursor.getString(eventCursor.getColumnIndex("sync_data4"));
                String eventsync_data5 = eventCursor.getString(eventCursor.getColumnIndex("sync_data5"));
                String eventsync_data6 = eventCursor.getString(eventCursor.getColumnIndex("sync_data6"));
                String eventsync_data7 = eventCursor.getString(eventCursor.getColumnIndex("sync_data7"));
                String eventsync_data8 = eventCursor.getString(eventCursor.getColumnIndex("sync_data8"));
                String eventsync_data9 = eventCursor.getString(eventCursor.getColumnIndex("sync_data9"));
                String eventsync_data10 = eventCursor.getString(eventCursor.getColumnIndex("sync_data10"));
                String eventlastSynced = eventCursor.getString(eventCursor.getColumnIndex("lastSynced"));
                String eventdtstart = eventCursor.getString(eventCursor.getColumnIndex("dtstart"));
                String eventdtend= eventCursor.getString(eventCursor.getColumnIndex("dtend"));
                String eventduration = eventCursor.getString(eventCursor.getColumnIndex("duration"));
                String eventeventTimezone= eventCursor.getString(eventCursor.getColumnIndex("eventTimezone"));
                String eventeventEndTimezone = eventCursor.getString(eventCursor.getColumnIndex("eventEndTimezone"));
                String eventallDay = eventCursor.getString(eventCursor.getColumnIndex("allDay"));
                String eventaccessLevel = eventCursor.getString(eventCursor.getColumnIndex("accessLevel"));
                String eventlastDate = eventCursor.getString(eventCursor.getColumnIndex("lastDate"));
                String eventrdate = eventCursor.getString(eventCursor.getColumnIndex("rdate"));
                String eventrrule = eventCursor.getString(eventCursor.getColumnIndex("rrule"));

                String CalendarID = eventCursor.getString(eventCursor.getColumnIndex("calendar_id"));//CalendarContract.EventsColumns.CALENDAR_ID
                Toast.makeText(CalendarDemo2.this, CalendarID + "::::" + eventTitle, Toast.LENGTH_LONG).show();
                Log.e("Test the Event","" +
                        "eventTitle:"+eventTitle+"\n"+
                        "eventeventColor:"+eventeventColor+"\n"+
                        "eventdescription:"+eventdescription+"\n"+
                        "eventeventLocation:"+eventeventLocation+"\n"+
                        "eventeventColor_index:"+eventeventColor_index+"\n"+
                        "eventselfAttendeeStatus:"+eventselfAttendeeStatus+"\n"+
                        "eventsync_data1:"+eventsync_data1+"\n"+
                        "eventsync_data2:"+eventsync_data2+"\n"+
                        "eventsync_data3:"+eventsync_data3+"\n"+
                        "eventsync_data4:"+eventsync_data4+"\n"+
                        "eventsync_data5:"+eventsync_data5+"\n"+
                        "eventsync_data6:"+eventsync_data6+"\n"+
                        "eventsync_data7:"+eventsync_data7+"\n"+
                        "eventsync_data8:"+eventsync_data8+"\n"+
                        "eventsync_data9:"+eventsync_data9+"\n"+
                        "eventsync_data10:"+eventsync_data10+"\n"+
                        "eventlastSynced:"+eventlastSynced+"\n"+
                        "eventdtend:"+eventdtend+"\n"+
                        "eventduration:"+eventduration+"\n"+
                        "eventeventTimezone:"+eventeventTimezone+"\n"+
                        "eventeventEndTimezone:"+eventeventEndTimezone+"\n"+
                        "eventallDay:"+eventallDay+"\n"+
                        "eventlastDate:"+eventlastDate+"\n"+
                        "eventrdate:"+eventrdate+"\n"+
                        "eventrrule:"+eventrrule+"\n");
            }
        } else {
            Toast.makeText(this, "该ID没有事件", Toast.LENGTH_SHORT).show();
        }
    }


}
