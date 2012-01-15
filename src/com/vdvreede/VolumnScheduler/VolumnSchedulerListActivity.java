package com.vdvreede.VolumnScheduler;

import java.util.Calendar;

import com.vdvreede.VolumnScheduler.Models.Schedule;

import android.app.Dialog;
import android.app.ListActivity;

import android.app.TimePickerDialog;

import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ContextMenu.ContextMenuInfo;

import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author paul
 * 
 */
public class VolumnSchedulerListActivity extends ListActivity {

	private VSAdapterDb mDbHelper;

	private final String TAG = "VSListActivity";

	private static final int DIALOG_QUIET_SET = 11;

	public static final int SILENT_ENABLED_NOTIFICATION = 44489;

	private static final int ACTIVITY_CREATE = 0;
	private static final int SCHEDULE_DELETE = 11;
	private static final int SCHEDULE_TOGGLE = 13;
	private static final int SCHEDULE_EDIT = 12;

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hours, int minutes) {
			Intent intent = new Intent(getApplicationContext(),
					DisableVolumnReceiver.class);
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
		this.setContentView(R.layout.vs_mainlist);
		this.mDbHelper = new VSAdapterDb(this);
		this.mDbHelper.open();
		this.fillData();
		this.registerForContextMenu(this.getListView());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "Item with id " + id + " clicked.");
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, VSEditActivity.class);
		i.putExtra(Schedule.KEY_ROWID, id);
		startActivityForResult(i, SCHEDULE_EDIT);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, SCHEDULE_DELETE, 0, R.string.context_menu_delete);
		menu.add(0, SCHEDULE_TOGGLE, 0, R.string.context_menu_toggle);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SCHEDULE_DELETE:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteSchedule(info.id);
			fillData();
			return true;
		case SCHEDULE_TOGGLE:
			AdapterContextMenuInfo info1 = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.toggleSchedule(info1.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_QUIET_SET:
			TimePickerDialog tpd = new TimePickerDialog(this, mTimeSetListener,
					0, 0, true);
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
		this.fillData();
	}

	private void fillData() {
		Cursor schedCursor = mDbHelper.getAllSchedules();
		Log.d(TAG,
				"Got all schedules with count of: "
						+ Integer.toString(schedCursor.getCount()));
		startManagingCursor(schedCursor);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { Schedule.KEY_NAME, Schedule.KEY_START,
				Schedule.KEY_END, Schedule.KEY_ACTIVE };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.tv_name, R.id.tv_start, R.id.tv_end,
				R.id.tv_active };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter schedules = new SimpleCursorAdapter(this,
				R.layout.vs_mainlist_row, schedCursor, from, to);
		setListAdapter(schedules);
	}
}