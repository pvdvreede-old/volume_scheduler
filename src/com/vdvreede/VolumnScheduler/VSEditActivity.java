package com.vdvreede.VolumnScheduler;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

public class VSEditActivity extends Activity {

	private EditText mNameText;
    private TimePicker mStartText;
    private TimePicker mEndText;
    private Long mRowId;
    private VSAdapterDb mDbHelper;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new VSAdapterDb(this);
        mDbHelper.open();

        setContentView(R.layout.vs_edit);
        setTitle(R.string.edit_schedule);

        mNameText = (EditText) findViewById(R.id.tb_name);
        mStartText = (TimePicker) findViewById(R.id.tp_start);
        mEndText = (TimePicker) findViewById(R.id.tp_end);
        
        Button saveButton = (Button) findViewById(R.id.bt_edit_save);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(VSAdapterDb.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(VSAdapterDb.KEY_ROWID)
									: null;
		}

		//populateFields();

		saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saveState();
        outState.putSerializable(VSAdapterDb.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor schedule = mDbHelper.getSchedule(mRowId);
            startManagingCursor(schedule);
//            mTitleText.setText(schedule.getString(
//            		schedule.getColumnIndexOrThrow(VSAdapterDb.KEY_TITLE)));
//            mBodyText.setText(schedule.getString(
//            		schedule.getColumnIndexOrThrow(VSAdapterDb.KEY_BODY)));
        }
    }
    

}
