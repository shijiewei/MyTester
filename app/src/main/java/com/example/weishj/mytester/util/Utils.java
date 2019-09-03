package com.example.weishj.mytester.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;

public class Utils {

	public static void openSpecifiedApp(Activity activity, int requestCode, String pkgName) {
		PackageManager packageManager = activity.getPackageManager();
		Intent it = packageManager.getLaunchIntentForPackage(pkgName);
		activity.startActivityForResult(it, requestCode);
	}

	public static File[] getExternalFilesDirs(Context context, String type) {
		if (Build.VERSION.SDK_INT >= 19) {
			return context.getExternalFilesDirs(type);
		} else {
			return new File[] { context.getExternalFilesDir(type) };
		}
	}

	public static File[] getExternalCacheDirs(Context context) {
		if (Build.VERSION.SDK_INT >= 19) {
			return context.getExternalCacheDirs();
		} else {
			return new File[] { context.getExternalCacheDir() };
		}
	}
}
