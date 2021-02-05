package com.example.weishj.mytester.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mob.tools.MobLog;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.UIHandler;

import java.io.File;

public class Utils {
	private static Context context;

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

	public static Context getContext() {
		if (context == null) {
			try {
				Object actThread = currentActivityThread();
				if (actThread != null) {
					Context app = ReflectHelper.invokeInstanceMethod(actThread, "getApplication");
					if (app != null) {
						context = app;
					}
				}
			} catch (Throwable t) {
				MobLog.getInstance().w(t);
			}
		}
		return context;
	}

	/** 获取当前进程的ActivityThread对象 */
	public static Object currentActivityThread() {
		Object activityThread;
		final ReflectHelper.ReflectRunnable<Void, Object> mainThreadAct = new ReflectHelper.ReflectRunnable<Void, Object>() {
			public Object run(Void arg) {
				try {
					//#if def{debuggable}
					String clzName = ReflectHelper.importClass("android.app.ActivityThread");
					//#else
					//#=String clzName = ReflectHelper.importClass(Strings.getString(31));
					//#endif
					//#if def{debuggable}
					return ReflectHelper.invokeStaticMethod(clzName, "currentActivityThread");
					//#else
					//#=return ReflectHelper.invokeStaticMethod(clzName, Strings.getString(32));
					//#endif
				} catch (Throwable t) {
					MobLog.getInstance().w(t);
				}
				return null;
			}
		};
		// 当前在主线程，或者系统版本>=18(Android 4.3)的子线程上，就直接在当前线程中获取ActivityThread对象
		if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId() || Build.VERSION.SDK_INT >= 18) {
			activityThread = mainThreadAct.run(null);
			if (activityThread != null) {
				return activityThread;
			}
		}
		// 如果是在系统版本<18的子线程上，必须切换到主线程获取ActivityThread对象
		final Object lock = new Object();
		final Object[] output = new Object[1];
		synchronized (lock) {
			UIHandler.sendEmptyMessage(0, new Handler.Callback() {
				public boolean handleMessage(Message msg) {
					synchronized (lock) {
						try {
							output[0] = mainThreadAct.run(null);
						} catch (Throwable t) {
							MobLog.getInstance().w(t);
						} finally {
							try {
								lock.notify();
							} catch (Throwable t) {
								MobLog.getInstance().w(t);
							}
						}
					}
					return false;
				}
			});
			try {
				lock.wait();
			} catch (Throwable t) {
				MobLog.getInstance().w(t);
			}
		}
		return output[0];
	}
}
