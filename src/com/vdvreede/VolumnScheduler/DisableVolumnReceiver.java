package com.vdvreede.VolumnScheduler;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

public class DisableVolumnReceiver extends BroadcastReceiver {

	public static final String TAG = "DisableVolumnReceiver";
	private static final int ENABLE_VOLUME_REQUEST = 778368;
	public static final int SILENT_ENABLED_NOTIFICATION = 44489;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive activated with intent: " + intent.toString());
		Bundle bundle = intent.getExtras();
		
		Calendar cal = Calendar.getInstance();
		switch (bundle.getInt("schedule_type")) {
		case 0:
			int hours = bundle.getInt("hours");
			int minutes = bundle.getInt("minutes");
			
			cal.add(Calendar.MINUTE, minutes);
			cal.add(Calendar.HOUR, hours);
		case 1:
			int end_hour = bundle.getInt("end_hours");
			int end_minute = bundle.getInt("end_minutes");
			
			cal.set(Calendar.HOUR_OF_DAY, end_hour);
			cal.set(Calendar.MINUTE, end_minute);
		}
		
		int ringerMode = bundle.getInt("ringer_mode");
		int setRinger;
		switch (ringerMode) {
		case 0:
			setRinger = AudioManager.RINGER_MODE_SILENT;
			break;
		case 1:
			setRinger = AudioManager.RINGER_MODE_VIBRATE;
			break;
		default:
			setRinger = AudioManager.RINGER_MODE_SILENT;
			break;
		}

		// set Alarm manager to callback in time period
		// get a Calendar object with current time
		
		Intent enableIntent = new Intent(context, EnableVolumnReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context,
				ENABLE_VOLUME_REQUEST, enableIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm_man = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm_man.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		// create notification with ability to cancel silence
		NotificationManager not_man = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.app_icon;
		CharSequence tickerText = "Volume Scheduler silence enabled.";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		CharSequence contentTitle = "Volume Scheduler";
		CharSequence contentText = "Silence enabled, expires at "
				+ cal.getTime().toLocaleString() + ". Press to cancel.";
		Intent notificationIntent = new Intent(context,
				EnableVolumnReceiver.class);
		PendingIntent contentIntent = PendingIntent.getBroadcast(context,
				ENABLE_VOLUME_REQUEST, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		not_man.notify(SILENT_ENABLED_NOTIFICATION, notification);

		// set volume to normal
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(setRinger);
		
		Log.d(TAG, "Quiet set to be deactivated at: " + cal.getTime().toLocaleString());
	}

}
