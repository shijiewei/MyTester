package com.example.weishj.mytester.collect;

import android.util.Log;

/**
 * Created by weishj on 2018/3/29.
 */

public class MyLog {
	public static void d(String tag, String msg) {
		Log.d(tag, "ThreadID: " + Thread.currentThread().getId() + ", msg: " + msg);
	}
}
