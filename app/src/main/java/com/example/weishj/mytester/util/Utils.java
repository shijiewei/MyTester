package com.example.weishj.mytester.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

public class Utils {

	public static void openSpecifiedApp(Activity activity, int requestCode, String pkgName) {
		PackageManager packageManager = activity.getPackageManager();
		Intent it = packageManager.getLaunchIntentForPackage(pkgName);
		activity.startActivityForResult(it, requestCode);
	}
}
