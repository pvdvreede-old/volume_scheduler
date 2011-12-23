package com.vdvreede.VolumnScheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.media.AudioManager;

public class EnableVolumnReceiver extends BroadcastReceiver {
	
	public static final String TAG = "EnableVoumnReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive activated with intent: " + intent.toString());
		// set volume to normal
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		
	}

}
