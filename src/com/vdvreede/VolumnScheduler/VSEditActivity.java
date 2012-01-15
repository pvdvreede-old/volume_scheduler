package com.vdvreede.VolumnScheduler;

import java.util.ArrayList;
import java.util.List;

import com.vdvreede.VolumnScheduler.Models.Schedule;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

public class VSEditActivity extends Activity {

	private EditText mNameText;
    private Button mStart;
    private Button mEnd;
    private CheckBox mMon;
    private CheckBox mTue;
    private CheckBox mWed;
    private CheckBox mThu;
    private CheckBox mFri;
    private CheckBox mSat;
    private CheckBox mSun;
    private Long mRowId;
    private VSAdapterDb mDbHelper;
    
    private static final int START_DIALOG = 78768;
    private static final int END_DIALOG = 45334; 
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new VSAdapterDb(this);
        mDbHelper.open();

        setContentView(R.layout.vs_edit);
        setTitle(R.string.edit_schedule);

        mNameText = (EditText) findViewById(R.id.tb_name);
        mStart = (Button) findViewById(R.id.bt_start_time);
        mEnd = (Button) findViewById(R.id.bt_end_time);
        
        mMon = (CheckBox) findViewById(R.id.cb_mon);
        mTue = (CheckBox) findViewById(R.id.cb_tue);
        mWed = (CheckBox) findViewById(R.id.cb_wed);
        mThu = (CheckBox) findViewById(R.id.cb_thu);
        mFri = (CheckBox) findViewById(R.id.cb_fri);
        mSat = (CheckBox) findViewById(R.id.cb_sat);
        mSun = (CheckBox) findViewById(R.id.cb_sun);
        
        Button saveButton = (Button) findViewById(R.id.bt_edit_save);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(Schedule.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(Schedule.KEY_ROWID)
									: null;
		}

		populateFields();

		saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {           	
                setResult(RESULT_OK);
                finish();
            }

        });
		
		mStart.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(START_DIALOG);
			}
		});
		mEnd.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(END_DIALOG);
			}
		});
    }
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(Schedule.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor schedCursor = mDbHelper.getSchedule(mRowId);
            startManagingCursor(schedCursor);
            Schedule sched = new Schedule(schedCursor);
            this.mNameText.setText(sched.name);
            this.mStart.setText("Start time: " + sched.start);
            this.mEnd.setText("End time: " + sched.start);
            this.populateCheckboxes(sched.days);
        }
    }
    
    private void populateCheckboxes(List<String> activeDays) {
    	for (String day : activeDays) {
    		if (day.equals("Mon")) {
    			mMon.setChecked(true);
    		}
    		if (day.equals("Tue")) {
    			mTue.setChecked(true);
    		}
    		if (day.equals("Wed")) {
    			mWed.setChecked(true);
    		}
    		if (day.equals("Thu")) {
    			mThu.setChecked(true);
    		}
    		if (day.equals("Fri")) {
    			mFri.setChecked(true);
    		}
    		if (day.equals("Sat")) {
    			mSat.setChecked(true);
    		}
    		if (day.equals("Sun")) {
    			mSun.setChecked(true);
    		}
    	}
    }
    
    private List<String> getDaysArray() {
    	List<String> days = new ArrayList<String>();
    	if (mMon.isChecked()) {
    		days.add("Mon");
    	}
    	if (mTue.isChecked()) {
    		days.add("Tue");
    	}
    	if (mWed.isChecked()) {
    		days.add("Wed");
    	}
    	if (mThu.isChecked()) {
    		days.add("Thu");
    	}
    	if (mFri.isChecked()) {
    		days.add("Fri");
    	}
    	if (mSat.isChecked()) {
    		days.add("Sat");
    	}
    	if (mSun.isChecked()) {
    		days.add("Sun");
    	}
    	
    	return days;
    }
    
    private void saveState() {
    	String startText = this.mStart.getText().toString();
    	String endText = this.mEnd.getText().toString();
    	int start = Integer.parseInt(startText.replace("Start time: ", "").replace(":", ""));
    	int end = Integer.parseInt(endText.replace("End time: ", "").replace(":", ""));
    	String name = this.mNameText.getText().toString();  	
    	Schedule schedule = new Schedule(0, null, name, String.valueOf(start), String.valueOf(end), this.getDaysArray());
    	this.mDbHelper.createSchedule(schedule);
    }
    
    protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DIALOG:
			TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
				
				public void onTimeSet(TimePicker view, int hours, int minutes) {
					// TODO Auto-generated method stub
					mStart.setText("Start time: " + Integer.toString(hours) + ":" + Integer.toString(minutes));
				}
			}, 12, 12, true);
			tpd.setTitle("Set start time");
			return tpd;
			
		case END_DIALOG:
			TimePickerDialog tpd1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
				
				public void onTimeSet(TimePicker view, int hours, int minutes) {
					// TODO Auto-generated method stub
					mEnd.setText("End time: " + Integer.toString(hours) + ":" + Integer.toString(minutes));
				}
			}, 12, 12, true);
			tpd1.setTitle("Set end time");
			return tpd1;
		}
		return null;
	}
}
