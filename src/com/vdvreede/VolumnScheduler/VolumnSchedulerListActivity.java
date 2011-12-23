package com.vdvreede.VolumnScheduler;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;

public class VolumnSchedulerListActivity extends ListActivity {

	private VSAdapterDb mDbHelper;

	private final String TAG = "VSListActivity";

	private static final int DIALOG_QUIET_SET = 11;

	public static final int SILENT_ENABLED_NOTIFICATION = 44489;

	private static final int ACTIVITY_CREATE = 0;

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hours, int minutes) {		
			Intent intent = new Intent(getApplicationContext(), DisableVolumnReceiver.class);
			intent.putExtra("hours", hours);
			intent.putExtra("minutes", minutes);
			intent.putExtra("ringer_mode", 0);
			
			getApplicationContext().sendBroadcast(intent);			
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_mainlist);
		this.mDbHelper = new VSAdapterDb(this);
		this.mDbHelper.open();
		this.fillData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_ab, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_schedule:
			this.createSchedule();
			return true;
		case R.id.schedule_quiet:
			this.showDialog(DIALOG_QUIET_SET);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_QUIET_SET:
			TimePickerDialog tpd = new TimePickerDialog(this, mTimeSetListener, 0, 0, true);
			tpd.setTitle("Set silence period");
			return tpd;
		}
		return null;
	}

	private void createSchedule() {
		Intent i = new Intent(this, VSEditActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	private void fillData() {
		Cursor schedCursor = mDbHelper.getAllSchedules();
		Log.d(TAG,
				"Got all schedules with count of: "
						+ Integer.toString(schedCursor.getCount()));
		startManagingCursor(schedCursor);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { VSAdapterDb.KEY_NAME,
				VSAdapterDb.KEY_ACTIVE, VSAdapterDb.KEY_START };// + " = " +
																// VSAdapterDb.COL_END};

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.tv_name, R.id.cb_active, R.id.tv_details };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter schedules = new SimpleCursorAdapter(this,
				R.layout.vs_mainlist_row, schedCursor, from, to);
		setListAdapter(schedules);
	}
}