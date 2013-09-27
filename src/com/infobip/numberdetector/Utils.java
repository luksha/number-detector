package com.infobip.numberdetector;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class Utils {

	private static final String PREFERENCE_NAME = "NUMBER_DETECTOR";
	private static final String DEVICE_ID = "DEVICE_ID";
	private static final String TAG = "Utils";
	
	public static String getRegesteredDeviceId(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String deviceId = prefs.getString(DEVICE_ID, /*default*/ null);
		
		if (!TextUtils.isEmpty(deviceId)) {
			Log.d("Current device id: " + deviceId, TAG);
			return deviceId;
		} else {
            Log.d("Error during saving device id. Device id is null or empty.", TAG);
		}
		return null;
	}
}
