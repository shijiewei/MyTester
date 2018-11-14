package com.example.weishj.mytester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.example.weishj.mytester.collect.BaseClt;
import com.example.weishj.mytester.collect.MyLog;
import com.example.weishj.mytester.handler.Sub;
import com.example.weishj.mytester.ui.CustomizedViewActivity;
import com.example.weishj.mytester.ui.MemoryLeakActivity;
import com.example.weishj.mytester.ui.RouterActivity;
import com.example.weishj.mytester.ui.WlanActivity;
import com.example.weishj.mytester.util.CmccAESTest;
import com.mob.tools.utils.Hashon;

import java.util.HashMap;

public class MainActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mContext = getApplicationContext();

		testHandler();

		findViewById(R.id.btn_handler).setOnClickListener(this);
		findViewById(R.id.btn_memory_leak).setOnClickListener(this);
		findViewById(R.id.btn_router).setOnClickListener(this);
		findViewById(R.id.btn_wlan).setOnClickListener(this);
		findViewById(R.id.btn_customizedView).setOnClickListener(this);

		// CMCCAESTest
		String key = "8F39703DD5D64A6D";
		String crypted = "OSxBYLA0FtMYtQMNMLBhvGmsNKXnrGfZLDgZ3r9RTfG9xnWnHdpkrUfrjiR+ fnZrfz2VksYvfYVYATaGdsyv8wm1qor6zRTaYXgDbFEiJRGeELAVdz+ps4q5 2og6ZiIY2j2dyObDIZ2ZMmD9+lObxdW/1dvyEmeHYleuuluanJW+70aj7KEo kj/BYL5UTMdgHHUQtp33HI2DIxg2St1+eIRUhZXs63hRFcqP7d9rQJIZe3Qx fTrs4lDxSyRHwYosVMEt5OSd8f6Izcl7RI2yCIB4orDPWJRhNPuYS1K5SXZO LJBNAZhXL9dhYZYN9kgzby7OnmcZH+5QA5oggh7DGkL/OIS1vJcHq9Oy9RUB nHxioHTQow9+LS3VnwC6afW6wliG50vhpG0Hjtkw3mcIBSwXkaGUstdznJEs st/qCVl3T38ULyrlr16k5bOhvMbuo/CIOiR5Mrse8F29KykYIk/zbBaMI+ew veLW/4pbwbM=";
		Log.e("jackie", "decode= " + CmccAESTest.AESDecode(key, crypted));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_handler) {
			MyLog.d(TAG, "btn_handler clicked");
			BaseClt.startCollectors(mContext);
		} else if (id == R.id.btn_memory_leak) {
			MyLog.d(TAG, "btn_memory_leak clicked");
			Intent i = new Intent(this, MemoryLeakActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_router) {
			MyLog.d(TAG, "btn_router clicked");
			Intent i = new Intent(this, RouterActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_wlan) {
			MyLog.d(TAG, "btn_wlan clicked");
			Intent i = new Intent(this, WlanActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_customizedView) {
			MyLog.d(TAG, "btn_customizedView clicked");
			Intent i = new Intent(this, CustomizedViewActivity.class);
			startActivity(i);
		}
	}

	private void testHandler() {
		Sub sub = new Sub();
		sub.startThread();
	}
}
