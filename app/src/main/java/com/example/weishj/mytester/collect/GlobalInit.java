package com.example.weishj.mytester.collect;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class GlobalInit {
	private static final String TAG = GlobalInit.class.getSimpleName();

	public static void init(Context context) {
		/**
		 * 1. Settings.System.putString() needs permission "android.permission.WRITE_SETTINGS"
		 * 2. WRITE_SETTINGS is a protected permission, only system app can use since android 6.0
		 */
		if (Build.VERSION.SDK_INT < 23) {
			boolean set = Settings.System.putString(context.getContentResolver(), "test_duid", "jackie");
			String testDuid = Settings.System.getString(context.getContentResolver(), "test_duid");
			Log.d(TAG, "set: " + set + ", testDuid: " + testDuid);
		} else {
			Log.d(TAG, "System version >= 23");
			if (Settings.System.canWrite(context)) {
				Log.d(TAG, "Can write settings");
				boolean set = Settings.System.putString(context.getContentResolver(), "test_duid", "jackie");
				String testDuid = Settings.System.getString(context.getContentResolver(), "test_duid");
				Log.d(TAG, "set: " + set + ", testDuid: " + testDuid);
			} else {
				Log.d(TAG, "Can not write settings");
			}
		}

	}
}
