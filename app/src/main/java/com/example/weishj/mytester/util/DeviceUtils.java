package com.example.weishj.mytester.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class DeviceUtils {
	private Context context;
	private static volatile DeviceUtils instance;

	private DeviceUtils(Context context) {
		if (context != null) {
			this.context = context.getApplicationContext();
		}
	}

	public static DeviceUtils getInstance(Context context) {
		if (instance == null) {
			synchronized (DeviceUtils.class) {
				if (instance == null) {
					instance = new DeviceUtils(context);
				}
			}
		}
		return instance;
	}

	/**
	 * 获取nfc信息，包含 是否支持nfc、是否支持hce-nfc、是否打开nfc、使用nfc的app、用app如果注册多个nfc支付服务，选择策略（0：默认，1：总是询问，2：冲突时询问）
	 */
	public JSONObject getNfcInfo() {
		JSONObject jsonObject = new JSONObject();
		try {
			int supportNfc = 0;
			int supportHce = 0;
			jsonObject.put("supportNfc", supportNfc);
			jsonObject.put("supportHce", 0);
			jsonObject.put("enable", 0);

			PackageManager packageManager = context.getPackageManager();
			NfcAdapter defaultAdapter = null;
			if (packageManager != null) {
				int sdk = Build.VERSION.SDK_INT;
				if (sdk >= 10 && packageManager.hasSystemFeature("android.hardware.nfc")) {
					supportNfc = 1;
					jsonObject.put("supportNfc", supportNfc);
				}
				if (sdk >= 19 && context.getPackageManager().hasSystemFeature("android.hardware.nfc.hce")) {
					supportHce = 1;
					jsonObject.put("supportHce", supportHce);
				}
			}

			if (android.os.Build.VERSION.SDK_INT >= 10&&(supportNfc == 1 || supportHce == 1)) {
				NfcManager nfcManager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
				if (nfcManager != null) {
					defaultAdapter = nfcManager.getDefaultAdapter();
					if (defaultAdapter != null && defaultAdapter.isEnabled()) {
						jsonObject.put("enable", 1);
					}
				}
			}

			if (supportHce == 1 && packageManager != null) {
				List<PackageInfo> list = packageManager.getInstalledPackages(PackageManager.GET_SERVICES);
				if (list != null && list.size() > 0) {
					JSONArray jsonArray = null;
					for (PackageInfo packageInfo : list) {
						if (packageInfo != null) {
							ServiceInfo[] serviceInfos = packageInfo.services;
							if (serviceInfos != null) {
								for (ServiceInfo serviceInfo : serviceInfos) {
									if (serviceInfo != null) {
										try {
											Bundle bundle = packageManager.getServiceInfo(new ComponentName(serviceInfo.packageName, serviceInfo.name), PackageManager.GET_META_DATA).metaData;
											if (bundle != null && bundle.containsKey("android.nfc.cardemulation.host_apdu_service")) {
												if (jsonArray == null) {
													jsonArray = new JSONArray();
												}
												jsonArray.put(packageInfo.packageName);
												break;
											}
										} catch (Throwable th) {
										}
									}
								}
							}
						}
					}
					if (jsonArray != null) {
						jsonObject.put("supportApps", jsonArray);
					}
				}
			}

			if (android.os.Build.VERSION.SDK_INT >= 19&&supportHce == 1 && defaultAdapter != null) {
				CardEmulation cardEmulation = null;
				cardEmulation = CardEmulation.getInstance(defaultAdapter);
				if (cardEmulation != null) {
					int selection = cardEmulation.getSelectionModeForCategory(CardEmulation.CATEGORY_PAYMENT);
					jsonObject.put("paySelection", selection);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
}
