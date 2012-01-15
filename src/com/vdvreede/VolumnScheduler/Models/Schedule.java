package com.vdvreede.VolumnScheduler.Models;

import java.util.Arrays;
import java.util.List;

import android.database.Cursor;

public class Schedule {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_ACTIVE = "active";
	public static final String KEY_NAME = "name";
	public static final String KEY_DAYS = "days";
	public static final String KEY_START = "start_time";
	public static final String KEY_END = "end_time";
	
	public static final String SCHEDULE_TABLE = "schedule";
	
	public static final String DATABASE_CREATE = "CREATE TABLE "
			+ SCHEDULE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_ACTIVE
			+ " integer NOT NULL DEFAULT(0), " + KEY_NAME + " text NOT NULL, "
			+ KEY_DAYS + " text NOT NULL, " + KEY_START + " integer NOT NULL, "
			+ KEY_END + " integer NOT NULL)";
			
	public Long id;
	public String name;
	public String start;
	public String end;
	public int active;
	public List<String> days;

	public Schedule(int active, Long id, String name, String start, String end,
			String days) {
		this.id = id;
		this.name = name;
		this.start = start;
		this.end = end;
		this.active = active;
		this.convertDaysString(days);
	}
	
	public Schedule(int active, Long id, String name, String start, String end,
			List<String> days) {
		this.id = id;
		this.name = name;
		this.start = start;
		this.end = end;
		this.active = active;
		this.days = days;
	}
	
	public Schedule(Cursor schedCursor) {
		this.id = schedCursor.getLong(schedCursor.getColumnIndexOrThrow(Schedule.KEY_ROWID));
		this.name = schedCursor.getString(schedCursor.getColumnIndexOrThrow(Schedule.KEY_NAME));
		this.start = schedCursor.getString(schedCursor.getColumnIndexOrThrow(Schedule.KEY_START));
		this.end = schedCursor.getString(schedCursor.getColumnIndexOrThrow(Schedule.KEY_END));
		this.active = schedCursor.getInt(schedCursor.getColumnIndexOrThrow(Schedule.KEY_ACTIVE));
		this.convertDaysString(schedCursor.getString(schedCursor.getColumnIndexOrThrow(Schedule.KEY_DAYS)));
	}

	public void convertDaysString(String days) {
		String[] dayArray = days.split(",");
		this.days = Arrays.asList(dayArray);
	}
	
	public String convertDaysArray() {
		String days = "";
		for (String day : this.days) {
			days += day + ",";
		}
		return days.substring(0, days.length() - 1);
	}
}
