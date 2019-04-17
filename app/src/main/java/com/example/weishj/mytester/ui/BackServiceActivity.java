package com.example.weishj.mytester.ui;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.Hashon;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BackServiceActivity extends BaseActivity {
	private static final String TAG = BackServiceActivity.class.getSimpleName();
	private TextView backServiceTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_back_service);

		initView();
		getBackService(100);
	}

	private void initView() {
		backServiceTv = findViewById(R.id.back_service_tv);
	}

	private void getBackService(int maxNum) {
		try {
			// 获取后台进程API只对Android 8.0以下有效
			if (Build.VERSION.SDK_INT < 26) {
				ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
				// 设置一个默认Service的数量大小
				int defaultNum = 100;
				if (maxNum > 0) {
					defaultNum = maxNum;
				}
				// 通过调用ActivityManager的getRunningAppServices()方法获得系统里所有正在运行的服务
				List<ActivityManager.RunningServiceInfo> runServiceList = mActivityManager.getRunningServices(defaultNum);
				if (runServiceList != null && !runServiceList.isEmpty()) {
					Hashon hashon = new Hashon();
					ArrayList<HashMap<String, Object>> serviceInfoList = new ArrayList<HashMap<String, Object>>();
					for (ActivityManager.RunningServiceInfo runServiceInfo : runServiceList) {
						// 获得Service所在的进程的信息
//						int pid = runServiceInfo.pid; // service所在的进程ID号
//						int uid = runServiceInfo.uid; // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
						// 进程名，默认是包名或者由属性android：process指定
						String processName = runServiceInfo.process;
						// 该Service启动时的时间值
						long activeSince = runServiceInfo.activeSince;
						// 如果该Service是通过Bind方式连接，则clientCount代表了service连接客户端的数目
//						int clientCount = runServiceInfo.clientCount;
						// 获得该Service的组件信息 可能是pkgname/servicename
						ComponentName serviceCMP = runServiceInfo.service;
						String serviceName = serviceCMP.getShortClassName(); // service 的类名
						String pkgName = serviceCMP.getPackageName(); // 包名
						// 通过包名，获取应用名称、版本信息等
						PackageManager mPackageManager = this.getPackageManager(); // 获取PackagerManager对象;
						// 获取该pkgName的信息
						ApplicationInfo appInfo = mPackageManager.getApplicationInfo(pkgName, 0);
						PackageInfo pkgInfo = mPackageManager.getPackageInfo(pkgName, 0);
						// 非系统应用才需要统计
						if (!isSystemApp(pkgInfo)) {
							RunningServiceModel runService = new RunningServiceModel();
							runService.setAppLabel(appInfo.loadLabel(mPackageManager) + "");
							runService.setServiceName(serviceName);
							runService.setPkgName(pkgName);
							// 设置该service的组件信息
//							Intent intent = new Intent();
//							intent.setComponent(serviceCMP);
//							runService.setIntent(intent);
//							runService.setPid(pid);
							runService.setProcessName(processName);
							runService.setVersionName(pkgInfo.versionName);
							runService.setVersionCode(pkgInfo.versionCode);
							runService.setActiveSince(activeSince);

							HashMap<String, Object> map = hashon.fromJson(hashon.fromObject(runService));
							// 添加至集合中
							serviceInfoList.add(map);
						}
					}

					HashMap<String, Object> map = new HashMap<>();
					map.put("list", serviceInfoList);
					map.put("elapsedRealtime", SystemClock.elapsedRealtime());

					String json = hashon.fromHashMap(map);

					Log.d(TAG, "total service: " + runServiceList.size() + ", app service: " + serviceInfoList.size());
					Log.d(TAG, "service: " + json);
					backServiceTv.setText(json);
				}
			} else {
				Toast.makeText(this, "Android 8.0以上不支持", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "API Level: " + Build.VERSION.SDK_INT);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	private boolean isSystemApp(PackageInfo pi) {
		boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
		boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
		return isSysApp || isSysUpd;
	}

	private static class RunningServiceModel {
		private String appLabel;
		private String pkgName;
		private String versionName;
		private long versionCode;
		private String serviceName;
		private String processName;
		private long activeSince;

		public void setAppLabel(String appLabel) {
			this.appLabel = appLabel;
		}

		public void setPkgName(String pkgName) {
			this.pkgName = pkgName;
		}

		public void setVersionName(String versionName) {
			this.versionName = versionName;
		}

		public void setVersionCode(long versionCode) {
			this.versionCode = versionCode;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public void setActiveSince(long activeSince) {
			this.activeSince = activeSince;
		}
	}
}
