package com.example.weishj.mytester;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.weishj.mytester.collect.BaseClt;
import com.example.weishj.mytester.collect.MyLog;
import com.example.weishj.mytester.fileobserver.FileWatcher;
import com.example.weishj.mytester.handler.Sub;
import com.example.weishj.mytester.ui.BackServiceActivity;
import com.example.weishj.mytester.ui.CustomizedViewActivity;
import com.example.weishj.mytester.ui.DeviceInfoActivity;
import com.example.weishj.mytester.ui.FileMonitorActivity;
import com.example.weishj.mytester.ui.FileShareActivity;
import com.example.weishj.mytester.ui.MemoryLeakActivity;
import com.example.weishj.mytester.ui.OtherTestActivity;
import com.example.weishj.mytester.ui.ReadContactActivity;
import com.example.weishj.mytester.ui.RouterActivity;
import com.example.weishj.mytester.ui.WlanActivity;
import com.example.weishj.mytester.util.CmccAESTest;
import com.mob.tools.utils.Hashon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mContext = getApplicationContext();

		checkPermissions();
//		testHandler();
		tempTestHere();

		findViewById(R.id.btn_other_test).setOnClickListener(this);
		findViewById(R.id.btn_handler).setOnClickListener(this);
		findViewById(R.id.btn_memory_leak).setOnClickListener(this);
		findViewById(R.id.btn_router).setOnClickListener(this);
		findViewById(R.id.btn_wlan).setOnClickListener(this);
		findViewById(R.id.btn_customizedView).setOnClickListener(this);
		findViewById(R.id.btn_file_monitor_test).setOnClickListener(this);
		findViewById(R.id.btn_backservice).setOnClickListener(this);
		findViewById(R.id.btn_file_share).setOnClickListener(this);
		findViewById(R.id.btn_read_contact).setOnClickListener(this);
		findViewById(R.id.btn_device_info).setOnClickListener(this);

		// CMCCAESTest
		String key = "8F39703DD5D64A6D";
		String crypted = "OSxBYLA0FtMYtQMNMLBhvGmsNKXnrGfZLDgZ3r9RTfG9xnWnHdpkrUfrjiR+ fnZrfz2VksYvfYVYATaGdsyv8wm1qor6zRTaYXgDbFEiJRGeELAVdz+ps4q5 2og6ZiIY2j2dyObDIZ2ZMmD9+lObxdW/1dvyEmeHYleuuluanJW+70aj7KEo kj/BYL5UTMdgHHUQtp33HI2DIxg2St1+eIRUhZXs63hRFcqP7d9rQJIZe3Qx fTrs4lDxSyRHwYosVMEt5OSd8f6Izcl7RI2yCIB4orDPWJRhNPuYS1K5SXZO LJBNAZhXL9dhYZYN9kgzby7OnmcZH+5QA5oggh7DGkL/OIS1vJcHq9Oy9RUB nHxioHTQow9+LS3VnwC6afW6wliG50vhpG0Hjtkw3mcIBSwXkaGUstdznJEs st/qCVl3T38ULyrlr16k5bOhvMbuo/CIOiR5Mrse8F29KykYIk/zbBaMI+ew veLW/4pbwbM=";
		Log.e("jackie", "decode= " + CmccAESTest.AESDecode(key, crypted));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_other_test) {
			Intent i = new Intent(this, OtherTestActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_handler) {
			BaseClt.startCollectors(mContext);
		} else if (id == R.id.btn_memory_leak) {
			Intent i = new Intent(this, MemoryLeakActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_router) {
			Intent i = new Intent(this, RouterActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_wlan) {
			Intent i = new Intent(this, WlanActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_customizedView) {
			Intent i = new Intent(this, CustomizedViewActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_file_monitor_test) {
			Intent i = new Intent(this, FileMonitorActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_backservice) {
			Intent i = new Intent(this, BackServiceActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_file_share) {
			Intent i = new Intent(this, FileShareActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_read_contact) {
			Intent i = new Intent(this, ReadContactActivity.class);
			startActivity(i);
		} else if (id == R.id.btn_device_info) {
			Intent i = new Intent(this, DeviceInfoActivity.class);
			startActivity(i);
		}
	}

	private void testHandler() {
		Sub sub = new Sub();
		sub.startThread();
	}

	/* 检查使用权限 */
	protected void checkPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			try {
				PackageManager pm = getPackageManager();
				PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
				ArrayList<String> list = new ArrayList<String>();
				for (String p : pi.requestedPermissions) {
					if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
						list.add(p);
					}
				}
				if (list.size() > 0) {
					String[] permissions = list.toArray(new String[list.size()]);
					if (permissions != null) {
						requestPermissions(permissions, 1);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	/**
	 * 临时测试的代码可以放在这里
	 */
	private void tempTestHere() {

	}
}
