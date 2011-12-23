package com.vdvreede.VolumnScheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VSAdapterDb {
	
	
	public static final String DATABASE_NAME = "VolumnScheduler";
	public static final String SCHEDULE_TABLE = "schedule";
	public static final int DATABASE_VERSION = 1;
	public static final String TAG = "VSAdapterDb";
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_ACTIVE = "active";
	public static final String KEY_NAME = "name";
	public static final String KEY_DAYS = "days";
	public static final String KEY_START = "start_time";
	public static final String KEY_END = "end_time";
	
	public static final String DATABASE_CREATE = 
			"CREATE TABLE " + SCHEDULE_TABLE + " (" +
			KEY_ROWID + " integer primary key autoincrement, " +
			KEY_ACTIVE + " integer NOT NULL DEFAULT(0), " +
			KEY_NAME + " text NOT NULL, " +
			KEY_DAYS + " text NOT NULL, " +
			KEY_START + " integer NOT NULL, " +
			KEY_END + " integer NOT NULL)";
	
	private final Context mContext;
	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb; 
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	Log.d(TAG, "Creating database with SQL: " + DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_TABLE);
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
		return mDb.query(SCHEDULE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_ACTIVE,
				KEY_DAYS, KEY_START, KEY_END}, null, null, null, null, null);
	}
	
	public Cursor getSchedule(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, SCHEDULE_TABLE, new String[] {KEY_ROWID,
            		KEY_NAME, KEY_ACTIVE,
    				KEY_DAYS, KEY_START, KEY_END}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
	
	public long createSchedule(String name, int start, int end) {
		ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_ACTIVE, 0);
        initialValues.put(KEY_START, start);
        initialValues.put(KEY_END, end);
        initialValues.put(KEY_DAYS, "test");

        return mDb.insert(SCHEDULE_TABLE, null, initialValues);
	}
	
}
