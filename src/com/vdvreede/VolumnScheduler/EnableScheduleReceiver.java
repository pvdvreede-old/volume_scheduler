package com.vdvreede.VolumnScheduler;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class EnableScheduleReceiver extends BroadcastReceiver {

	public static final int ENABLE_SCHEDULE_REQUEST = 11236;
	private static final long WEEK_IN_MILLISECS = 604800000;
	private final String TAG = "EnableSchedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Log.d(TAG, "onReceive activated with intent: " + intent.toString());
			// retrieve schedule id from bundle
			Bundle bundle = intent.getExtras();
			String schedule_name = bundle.getString("schedule_name");
			int[] intDays = bundle.getIntArray("schedule_days");
			int startHourOfDay = bundle.getInt("schedule_start_hour");
			int startMinuteOfDay = bundle.getInt("schedule_start_minute");
			int endHourOfDay = bundle.getInt("schedule_end_hour");
			int endMinuteOfDay = bundle.getInt("schedule_end_minute");
			
			AlarmManager alarm_man = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			
			// loop through all the days in the schedule and create alarms for each of them
			for (int day : intDays) {
				Calendar scheduleStartTime = Calendar.getInstance();
				scheduleStartTime.set(Calendar.DAY_OF_WEEK, day);
				scheduleStartTime.set(Calendar.HOUR_OF_DAY, startHourOfDay);
				scheduleStartTime.set(Calendar.MINUTE, startMinuteOfDay);
				scheduleStartTime.set(Calendar.SECOND, 0);
				
				Intent enableIntent = new Intent(context,
						DisableVolumnReceiver.class);
				
				enableIntent.putExtra("schedule_type", 1);
				enableIntent.putExtra("end_hours", endHourOfDay);
				enableIntent.putExtra("end_minutes", endMinuteOfDay);
				enableIntent.putExtra("start_day", day);
				
				PendingIntent sender = PendingIntent.getBroadcast(context,
						ENABLE_SCHEDULE_REQUEST, enableIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				
				alarm_man.setRepeating(AlarmManager.RTC_WAKEUP, scheduleStartTime.getTimeInMillis(), WEEK_IN_MILLISECS,
						sender);
				
				Log.d(TAG, "Schedule enabled to activate at: " + scheduleStartTime.getTime().toLocaleString());
			}

			// Send toast alert so user knows the schedule has been activated
			CharSequence text = "Schedule " + schedule_name + " enabled.";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			
			
		} catch (Exception ex) {
			// Send toast alert so user knows the schedule has been activated
			CharSequence text = "Error enabling schedule. " + ex.getMessage();
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

}
