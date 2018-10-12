package com.example.weishj.mytester.collect;

import android.content.Context;
import android.text.TextUtils;

import com.mob.tools.MobLog;
import com.mob.tools.utils.FileLocker;
import com.mob.tools.utils.ResHelper;

import java.io.File;

public class Locks {
	private static final String LOCKS_DIR = "comm/locks/";
	public static final String EVENT_RECORDER_LOCK = LOCKS_DIR + ".mrlock";
	public static final String DATA_HEAP_LOCK = LOCKS_DIR + ".dhlock";
	public static final String DUID_LOCK = LOCKS_DIR + ".globalLock";
	public static final String RUNTIME_COLLECTOR_LOCK = LOCKS_DIR + ".rc_lock";
	public static final String DEVICE_INFO_LOCK = LOCKS_DIR + ".dic_lock";
	public static final String PACKAGE_COLLECTOR_LOCK = LOCKS_DIR + ".pkg_lock";
	public static final String APP_RUNNING_TIME_COLLECTOR_LOCK = LOCKS_DIR + ".artc_lock";
	public static final String LIGHT_ELECTRIC_SIMULATOR_INFO = LOCKS_DIR + ".lesd_lock";

	public static synchronized File getFileLock(Context context, String name) {
		return new File(ResHelper.getCacheRoot(context), name);
	}

	public static boolean synchronizeProcess(File file, LockAction act) {
		return synchronizeProcess(file, true, act);
	}

	private static String checkAndGetLockName(String path) {
		if (!TextUtils.isEmpty(path)) {
			if (path.endsWith(DUID_LOCK)) {
				return DUID_LOCK;
			} else if (path.endsWith(DATA_HEAP_LOCK)) {
				return DATA_HEAP_LOCK;
			} else if (path.endsWith(EVENT_RECORDER_LOCK)) {
				return EVENT_RECORDER_LOCK;
			} else if (path.endsWith(RUNTIME_COLLECTOR_LOCK)) {
				return RUNTIME_COLLECTOR_LOCK;
			} else if (path.endsWith(APP_RUNNING_TIME_COLLECTOR_LOCK)) {
				return APP_RUNNING_TIME_COLLECTOR_LOCK;
			} else if (path.endsWith(LIGHT_ELECTRIC_SIMULATOR_INFO)) {
				return LIGHT_ELECTRIC_SIMULATOR_INFO;
			} else if (path.endsWith(DEVICE_INFO_LOCK)) {
				return DEVICE_INFO_LOCK;
			} else if (path.endsWith(PACKAGE_COLLECTOR_LOCK)) {
				return PACKAGE_COLLECTOR_LOCK;
			}
		}
		return path;
	}

	private static boolean synchronizeProcess(File lockFile, boolean block, LockAction act) {
		try {
			if (!lockFile.getParentFile().exists()) {
				lockFile.getParentFile().mkdirs();
			}
			if (!lockFile.exists()) {
				lockFile.createNewFile();
			}
			String path = lockFile.getAbsolutePath();
			String pathLock = checkAndGetLockName(path);
			synchronized (pathLock) {
				FileLocker lock = new FileLocker();
				lock.setLockFile(path);
				if (lock.lock(block)) {
					if (!act.run(lock)) {
						lock.release();
					}
				} else {
					return false;
				}
			}
		} catch (Throwable t) {
			MobLog.getInstance().w(t);
		}
		return true;
	}

}
