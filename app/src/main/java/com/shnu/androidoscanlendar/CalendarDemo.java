package com.shnu.androidoscanlendar;

import java.util.Calendar;
import java.util.TimeZone;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CalendarDemo extends Activity implements OnClickListener {
	private Button mReadUserButton;
	private Button mReadEventButton;
	private Button mWriteEventButton;
	private Button mNewApiButton;

	private static String calanderURL = "";
	private static String calanderEventURL = "";
	private static String calanderRemiderURL = "";

	//为了兼容不同版本的日历,2.2以后url发生改变
	static {
		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
			calanderURL = "content://com.android.calendar/calendars";
			calanderEventURL = "content://com.android.calendar/events";
			calanderRemiderURL = "content://com.android.calendar/reminders";

		} else {
			calanderURL = "content://calendar/calendars";
			calanderEventURL = "content://calendar/events";
			calanderRemiderURL = "content://calendar/reminders";
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		setupViews();
	}

	private void setupViews() {
		mReadUserButton = (Button) findViewById(R.id.readUserButton);
		mReadEventButton = (Button) findViewById(R.id.readEventButton);
		mWriteEventButton = (Button) findViewById(R.id.writeEventButton);
		mNewApiButton = (Button)findViewById(R.id.newAPI);
		mReadUserButton.setOnClickListener(this);
		mReadEventButton.setOnClickListener(this);
		mWriteEventButton.setOnClickListener(this);
		mNewApiButton.setOnClickListener(this);
	}

	@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onClick(View v) {
		if (v == mReadUserButton) {
			Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL), null,
					null, null, null);
//			if (userCursor.getCount() > 0) {
//				userCursor.moveToFirst();
//				String userName = userCursor.getString(userCursor.getColumnIndex("name"));
//				Toast.makeText(CalendarDemo.this, userName + userCursor.toString(), Toast.LENGTH_LONG).show();
//			}
			while(userCursor.moveToNext()){
				String userName = userCursor.getString(userCursor.getColumnIndex("name"));
				Log.e("Test the get ",userName+"\n");
			}

		} else if (v == mReadEventButton) {
			Cursor eventCursor = getContentResolver().query(Uri.parse(calanderEventURL), null,
					null, null, null);
//			if (eventCursor.getCount() > 0) {
//				eventCursor.moveToLast();
//				String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
//				Toast.makeText(CalendarDemo.this, eventTitle, Toast.LENGTH_LONG).show();
//			}
			while (eventCursor.moveToNext()){
				String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
				String eventDescription = eventCursor.getString(eventCursor.getColumnIndex("description"));
				String eventLocation = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
				String eventColor = eventCursor.getString(eventCursor.getColumnIndex("eventColor"));
				String eventColor_index = eventCursor.getString(eventCursor.getColumnIndex("eventColor_index"));
				Log.e("Test the get ",eventTitle+"\n"+eventDescription+"\n"+eventLocation+"\n"+eventColor+"\n"+eventColor_index+"\n");

			}


		} else if (v == mWriteEventButton) {
			//获取要出入的gmail账户的id
			String calId = "";
			Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL), null,
					null, null, null);
			if (userCursor.getCount() > 0) {
				userCursor.moveToFirst();
				calId = userCursor.getString(userCursor.getColumnIndex("_id"));

			}
			ContentValues event = new ContentValues();
			event.put("title", "与苍井空小姐动作交流");
			event.put("description", "Frankie受空姐邀请,今天晚上10点以后将在Sheraton动作交流.lol~");
			//插入hoohbood@gmail.com这个账户
			event.put("calendar_id", calId);
			event.put("eventTimezone", TimeZone.getDefault().getID());

			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(Calendar.HOUR_OF_DAY, 10);
			long start = mCalendar.getTime().getTime();
			mCalendar.set(Calendar.HOUR_OF_DAY, 11);
			long end = mCalendar.getTime().getTime();

			event.put("dtstart", start);
			event.put("dtend", end);
			event.put("hasAlarm", 1);

			Uri newEvent = getContentResolver().insert(Uri.parse(calanderEventURL), event);
			long id = Long.parseLong(newEvent.getLastPathSegment());
			ContentValues values = new ContentValues();
			values.put("event_id", id);
			//提前10分钟有提醒
			values.put("minutes", 10);
//			values.put("calendar_displayName","calendar_displayName");
			event.put("sync_data2",1);
			getContentResolver().insert(Uri.parse(calanderRemiderURL), values);
			Toast.makeText(CalendarDemo.this, "插入事件成功!!!", Toast.LENGTH_LONG).show();
		}else if(v == mNewApiButton ){
			createCalendar("Mycalendar",6);
		}
	}
//新建一个日历

	@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void createCalendar(String name, int color) {
		ContentValues values = new ContentValues();
		values.put(CalendarContract.Calendars.ACCOUNT_NAME, name);
		values.put(CalendarContract.Calendars.ACCOUNT_TYPE, "LOCAL");
		values.put(CalendarContract.Calendars.NAME, name);
		values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, name);
		/**显示事件颜色*/
		values.put(CalendarContract.Calendars.CALENDAR_COLOR, color);
		/**权限级别*/
		values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
		/**事件可见*/
		values.put(CalendarContract.Calendars.VISIBLE, 1);
		values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
		values.put(CalendarContract.Calendars.OWNER_ACCOUNT, name);
		/**注释的属性都是不可添加的，我们作为类似第三方的使用系统的数据没有拥有此属性的权限*/
//                        values.put("canModifyTimeZone",1);
//                        values.put("canPartiallyUpdate",0);
//                        values.put("maxReminders",5);
//                        values.put("allowedReminders","0,1");

//                        values.put("allowedAvailability","0,1");
//                        values.put("allowedAttendeeTypes","0,1,2");
//                        values.put("deleted",1);
		/**新增用户日程*/
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}


		//	getContentResolver().delete(CalendarContract.Calendars.CONTENT_URI," name = ?",new String[]{"Mycalendar"}); // 删除一个日历表


		Uri uri = getContentResolver().insert(CalendarContract.Calendars.CONTENT_URI, values);
		/**获取返回的新建日程ID*/
		Long calId= Long.parseLong(uri.getLastPathSegment());
		if(calId!=0){
			Toast.makeText(this, "新建日程成功",Toast.LENGTH_SHORT).show();
//			saveCalendarID(calId+"");
//			enterNext(calId+"");
		}else{
			Toast.makeText(this, "新建日程失败",Toast.LENGTH_SHORT).show();
		}
	}






}