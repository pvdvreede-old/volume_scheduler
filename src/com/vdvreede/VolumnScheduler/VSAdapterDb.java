package com.vdvreede.VolumnScheduler;

import com.vdvreede.VolumnScheduler.Models.Schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VSAdapterDb {

	public static final String DATABASE_NAME = "VolumnScheduler";
	
	public static final int DATABASE_VERSION = 1;
	public static final String TAG = "VSAdapterDb";

	private final Context mContext;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Creating database with SQL: " + Schedule.DATABASE_CREATE);
			db.execSQL(Schedule.DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + Schedule.SCHEDULE_TABLE);
			onCreate(db);
		}
	}

	public VSAdapterDb(Context ctx) {
		this.mContext = ctx;
	}

	public VSAdapterDb open() throws SQLException {
		this.mDbHelper = new DatabaseHelper(mContext);
		this.mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public Cursor getAllSchedules() {
		return mDb.query(Schedule.SCHEDULE_TABLE, new String[] { Schedule.KEY_ROWID, Schedule.KEY_NAME,
				Schedule.KEY_ACTIVE, Schedule.KEY_DAYS, Schedule.KEY_START, Schedule.KEY_END }, null, null, null,
				null, null);
	}

	public Cursor getSchedule(long rowId) throws SQLException {

		Cursor mCursor = mDb.query(true, Schedule.SCHEDULE_TABLE,
				new String[] { Schedule.KEY_ROWID, Schedule.KEY_NAME, Schedule.KEY_ACTIVE, Schedule.KEY_DAYS,
				Schedule.KEY_START, Schedule.KEY_END }, Schedule.KEY_ROWID + "=" + rowId, null,
				null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public long createSchedule(Schedule schedule) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(Schedule.KEY_NAME, schedule.name);
		initialValues.put(Schedule.KEY_ACTIVE, schedule.active);
		initialValues.put(Schedule.KEY_START, schedule.start);
		initialValues.put(Schedule.KEY_END, schedule.end);
		initialValues.put(Schedule.KEY_DAYS, schedule.convertDaysArray());
		Log.d(TAG, "New schedule being created.");
		return mDb.insert(Schedule.SCHEDULE_TABLE, null, initialValues);
	}
	
	public boolean deleteSchedule(long rowId) {
		
		return mDb.delete(Schedule.SCHEDULE_TABLE, Schedule.KEY_ROWID + "=" + rowId, null) > 0;
	
	}
	
}
