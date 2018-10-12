package com.example.weishj.mytester.collect;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mob.tools.MobHandlerThread;
import com.mob.tools.utils.FileLocker;

import java.io.File;
import java.util.HashMap;

/**
 * Created by weishj on 2018/3/29.
 */

public class BaseClt implements Handler.Callback {
	private static final String TAG = BaseClt.class.getSimpleName();
	private static HashMap<String, BaseClt> instanceMap = new HashMap<String, BaseClt>();
	private MobHandlerThread thread;
	private Handler handler;
	private static Context mContext;

	public static void startCollectors(Context context) {
		mContext = context;
		new Thread() {
			public void run() {
				MyLog.d(TAG, "startCollectors()");
				BaseClt.startCollectors(ArtClt.class, PkgClt.class);
			}
		}.start();
	}

	private static void startCollectors(Class<? extends BaseClt>... clzArray) {
		if (clzArray == null || clzArray.length == 0) {
			return;
		}
		for (Class<? extends BaseClt> clz : clzArray) {
			if (clz != null) {
				String className = clz.getSimpleName();
				BaseClt instance = instanceMap.get(className);
				if (instance == null) {
					try {
						instance = clz.newInstance();
						instanceMap.put(className, instance);
						MyLog.d(TAG, "准备开始 " + className + ".start()");
						instance.start();
					} catch (Throwable t) {
						MyLog.d(TAG, t.getMessage());
					}
				}
			}
		}
	}

	private void start() {
		final File file = getLockFile(mContext);
		if (file == null) {
			return;
		}
		thread = new MobHandlerThread() {
			public void run() {
				try {
					boolean result = Locks.synchronizeProcess(file, new LockAction() {
						public boolean run(FileLocker lock) {
							try {
								MyLog.d(TAG, "准备开始 runOfSuper()");
								runOfSuper();
							} catch (Throwable t) {
								MyLog.d(TAG, t.getMessage());
							}
							return false;
						}
					});
				} catch (Throwable t) {
					MyLog.d(TAG, t.getMessage());
				}
			}

			private void runOfSuper() {
				super.run();
			}

			protected void onLooperPrepared(Looper looper) {
				try {
					MyLog.d(TAG, "准备开始 initSendMsg()");
					handler = new Handler(looper, BaseClt.this);
					initSendMsg();
				} catch (Throwable t) {
					MyLog.d(TAG, t.getMessage());
				}
			}
		};
		thread.start();
	}

	public final boolean handleMessage(Message msg) {
//		boolean notAllowed = CommonConfig.isNotAllowedCollectRunning();
//		if(notAllowed) {
//			//如果不允许采集，则停止
//			stop();
//		} else {
//			handleMsg(msg);
//		}
		handleMsg(msg);
		return false;
	}

	final void removeMessages(int what) {
		if (handler != null) {
			handler.removeMessages(what);
		}
	}

	final void sendEmptyMessage(int what) {
		if (handler != null) {
			handler.sendEmptyMessage(what);
		}
	}

	final void sendEmptyMessageDelayed(int what, long delay) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(what, delay);
		}
	}

	final void sendMessage(Message msg) {
		if (handler != null) {
			handler.sendMessage(msg);
		}
	}

	final void sendMessageDelayed(Message msg, long delay) {
		if (handler != null) {
			handler.sendMessageDelayed(msg, delay);
		}
	}

	protected File getLockFile(Context context) { return null; }

	/* 初始化msg */
	protected void initSendMsg() {}

	/* 捕获msg */
	protected void handleMsg(Message msg) {}
}
