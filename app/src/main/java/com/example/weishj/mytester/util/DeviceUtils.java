package com.example.weishj.mytester.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.mob.tools.MobLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceUtils {
	private static final String TAG = "DeviceUtils";
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

	//ipv6
	public String getLocalIpV6() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// logger.error("ip1       " + inetAddress);
					Log.d(TAG, "ip1: " + inetAddress.getHostAddress());
                 /*   logger.error("getHostName  " + inetAddress.getHostName());
                    logger.error("getCanonicalHostName  " + inetAddress.getCanonicalHostName());
                    logger.error("getAddress  " + Arrays.toString(inetAddress.getAddress()));
                    logger.error("getHostAddress  " + inetAddress.getHostAddress());*/

					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
						return inetAddress.getHostAddress();
					}


				}
			}
		} catch (Exception ex) {
			Log.e("IP Address", ex.toString());
		}
		return null;
	}

	/**获取包名*/
	public String getPackageName() {
		return context.getPackageName();
	}

	public boolean checkPermission(String permission) throws Throwable {
		int res;
		if (Build.VERSION.SDK_INT >= 23) {
			try {
				res = context.checkSelfPermission(permission);
			} catch (Throwable t) {
				MobLog.getInstance().d(t);
				res = PackageManager.PERMISSION_DENIED;
			}
		} else {
			res = context.getPackageManager().checkPermission(permission, getPackageName());
		}
		return res == PackageManager.PERMISSION_GRANTED;
	}

	public String getlocalIp() {
		String ip;

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
//                           ip=inetAddress.getHostAddress().toString();
						System.out.println("ip==========" + inetAddress.getHostAddress());
						return inetAddress.getHostAddress();

					}
				}
			}
		} catch (SocketException ignored) {

		}
		return null;
	}

	public String validateV6() {
		String hostIp6 = getLocalIpV6();
		//过滤找到真实的ipv6地址
		Log.d(TAG, "v6 validateV6 " + hostIp6);
		if (hostIp6 != null && hostIp6.contains("%")) {
			String[] split = hostIp6.split("%");
			String s1 = split[0];
			Log.d(TAG, "v6 remove % is " + s1);

			if (s1 != null && s1.contains(":")) {
				String[] split1 = s1.split(":");
				if (split1.length == 6||split1.length==8) {
					if (split1[0].contains("fe") || split1[0].contains("fc")) {
						return "0.0.0.0";
					} else {
						return s1;
					}
				}
			}
		}
		return "0.0.0.0";
	}

	/**
	 * 获取默认输入法
	 *
	 * 不需要 任何权限
	 *
	 * android 11 测试可行
	 *
	 * @param context
	 * @return
	 */
	public String getDefaultInputMethodPkgName(Context context) {
		String mDefaultInputMethodPkg = null;

		String mDefaultInputMethodCls = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.DEFAULT_INPUT_METHOD);
		//输入法类名信息
		Log.d(TAG, "mDefaultInputMethodCls=" + mDefaultInputMethodCls);
		if (!TextUtils.isEmpty(mDefaultInputMethodCls)) {
			//输入法包名
			mDefaultInputMethodPkg = mDefaultInputMethodCls.split("/")[0];
			Log.d(TAG, "mDefaultInputMethodPkg=" + mDefaultInputMethodPkg);
		}
		return mDefaultInputMethodPkg;
	}

	/**
	 * 获取指定包名的应用名
	 *
	 * @param pkg
	 * @return
	 */
	public String getAppName(String pkg) {
		String name = null;
		try {
			if (!TextUtils.isEmpty(pkg)) {
				PackageManager pm = context.getPackageManager();
				PackageInfo pi = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
				name = pi.applicationInfo.loadLabel(pm).toString();
			}
		} catch (Throwable t) {
			MobLog.getInstance().d(t);
		}
		return name;
	}

	/**
	 * 获取输入法列表
	 *
	 * 不需要 任何 权限
	 *
	 * android 11 测试可行
	 */
	public void getInputMethodList(){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> methodList = imm.getInputMethodList();
		for(InputMethodInfo mi : methodList ) {
			CharSequence name = mi.loadLabel(context.getPackageManager());
			Log.d(TAG, "getInputMethodList. name: "+ name + ", pkg: " + mi.getPackageName());
		}
	}

	/**
	 * 判断设备 是否使用代理上网
	 *
	 * 不需要 任何 权限
	 *
	 * android 11 测试可行
	 *
	 * */
	public boolean isWifiProxy(Context context) {
		final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
		String proxyAddress;
		int proxyPort;
		if (IS_ICS_OR_LATER) {
			proxyAddress = System.getProperty("http.proxyHost");
			String portStr = System.getProperty("http.proxyPort");
			proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
		} else {
			proxyAddress = android.net.Proxy.getHost(context);
			proxyPort = android.net.Proxy.getPort(context);
		}
		return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
	}

	/**
	 * 获取设备IP（wifi/cellular）
	 *
	 * 需要 INTERNET 权限，无权限时获取不到值，不抛异常
	 *
	 * android 11 测试可行
	 *
	 * https://www.jianshu.com/p/be244fb85a4e
	 * @return
	 */
	public String getIp() {
		try {
			if (checkPermission("android.permission.INTERNET")) {
				Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces();
				if (enNetI != null) {
					while (enNetI.hasMoreElements()) {
						NetworkInterface netI = enNetI.nextElement();
						Enumeration<InetAddress> enumIpAddr = netI.getInetAddresses();
						if (enumIpAddr != null) {
							while (enumIpAddr.hasMoreElements()) {
								InetAddress inetAddress = enumIpAddr.nextElement();
								if (inetAddress != null && inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
									return inetAddress.getHostAddress();
								}
							}
						}
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return "";
	}

	/**
	 *
	 * 获取设备网关地址
	 *
	 * https://blog.csdn.net/shaoenxiao/article/details/81285464
	 *
	 * 需要权限 ACCESS_WIFI_STATE，无权限会抛异常
	 *
	 * android 11 测试可行
	 *
	 * @return
	 */
	public String getWifiGateway() {
		String ip = null;
		try {
			if (checkPermission("android.permission.ACCESS_WIFI_STATE")) {
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				DhcpInfo info=wifiManager.getDhcpInfo();
				int gateway=info.gateway;
				ip=intToIp(gateway);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return ip;
	}

	private String intToIp(int addr) {
		return ((addr & 0xFF) + "." +
				((addr >>>= 8) & 0xFF) + "." +
				((addr >>>= 8) & 0xFF) + "." +
				((addr >>>= 8) & 0xFF));
	}

	/**
	 * 根据adb shell命令获取getprop中的信息(WIFI或dhcp下)
	 *
	 * 权限需求 未确认
	 *
	 * android 11 测试不可行
	 *
	 * @return
	 */
	public String getGateway() {
		BufferedReader bufferedReader = null;
		String str2 = "";
		String str3 = "getprop dhcp.eth0.gateway";
		Process exec;
		BufferedReader bufferedReader2 = null;
		try {
			exec = Runtime.getRuntime().exec(str3);
			try {
				bufferedReader2 = new BufferedReader(new InputStreamReader(exec.getInputStream()));
			} catch (Throwable th3) {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (exec != null) {
					exec.exitValue();
				}
			}
			try {
				str3 = bufferedReader2.readLine();
				if (str3 != null) {
					TextUtils.isEmpty(str3);
				}
				try {
					bufferedReader2.close();
				} catch (IOException iOException222) {
					iOException222.printStackTrace();
				}
				if (exec != null) {
					try {
						exec.exitValue();
					} catch (Exception e5) {
					}
				}
			} catch (IOException e6) {
				str3 = str2;
				if (bufferedReader2 != null) {
					bufferedReader2.close();
				}
				if (exec != null) {
					exec.exitValue();
				}
				return str3;
			}
		} catch (IOException e62) {
			bufferedReader2 = null;
			exec = null;
			str3 = str2;
			if (bufferedReader2 != null) {
				try {
					bufferedReader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (exec != null) {
				exec.exitValue();
			}
			return str3;
		} catch (Throwable th4) {
			exec = null;
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (exec != null) {
				exec.exitValue();
			}
		}
		return str3;
	}

	/**
	 * 根据adb shell命令获取网关信息（static下）
	 *
	 * 不需要 任何 权限
	 *
	 * android 11 测试可行
	 *
	 * @return
	 */
	public String getGatewayForStatic() {
		BufferedReader bufferedReader = null;
		String result="";
		String str2 = "";
		String str3 = "ip route list table 0";
		Process exec;
		BufferedReader bufferedReader2 = null;
		try {
			exec = Runtime.getRuntime().exec(str3);
			try {
				bufferedReader2 = new BufferedReader(new InputStreamReader(exec.getInputStream()));
			} catch (Throwable th3) {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (exec != null) {
					exec.exitValue();
				}
			}
			try {
				while ((str2 = bufferedReader2.readLine()) != null) {
					if (str2.contains("default via")) {
						str2= str2.trim();
						String[] strings=str2.split("\\s+");
						if (strings.length>3){
							result= strings[2];
						}
						break;
					}
				}
				try {
					bufferedReader2.close();
				} catch (IOException iOException222) {
					iOException222.printStackTrace();
				}
				if (exec != null) {
					try {
						exec.exitValue();
					} catch (Exception e5) {
					}
				}
			} catch (IOException e6) {
				if (bufferedReader2 != null) {
					bufferedReader2.close();
				}
				if (exec != null) {
					exec.exitValue();
				}
				return result;
			}
		} catch (IOException e62) {
			bufferedReader2 = null;
			exec = null;
			if (bufferedReader2 != null) {
				try {
					bufferedReader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (exec != null) {
				exec.exitValue();
			}
			return result;
		} catch (Throwable th4) {
			exec = null;
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (exec != null) {
				exec.exitValue();
			}
		}
		return result;
	}

	/**
	 * 获取设备内存信息
	 *
	 * 不需要 任何 权限
	 *
	 * Android 11 测试可行
	 */
	public void getDeviceMemo() {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		// 设备可用内存（单位Byte）
		long avail = mi.availMem;
		// 设备总内存（单位Byte）
		long total = mi.totalMem;
		boolean low = mi.lowMemory;
		long threshold = mi.threshold;
		Log.d(TAG, "DeviceMemory. avail: " + avail);
		Log.d(TAG, "DeviceMemory. total: " + total);
		Log.d(TAG, "DeviceMemory. low: " + low);
		Log.d(TAG, "DeviceMemory. threshold: " + threshold);
	}

	/**
	 *
	 * 获取设备内存信息
	 *
	 * 不需要 任何 权限
	 *
	 * Android 11 测试可行
	 *
	 * https://www.cnblogs.com/helloandroid/articles/2210334.html
	 *
	 * MemTotal: 总内存大小。
	 * MemFree: LowFree与HighFree的总和，被系统留着未使用的内存。
	 * MemAvailable：可用内存
	 * Buffers: 用来给文件做缓冲大小。
	 * Cached: 被高速缓冲存储器（cache memory）用的内存的大小（等于diskcache minus SwapCache）。
	 * SwapCached:被高速缓冲存储器（cache memory）用的交换空间的大小。已经被交换出来的内存，仍然被存放在swapfile中，用来在需要的时候很快的被替换而不需要再次打开I/O端口。
	 * Active: 在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要，否则不会被移作他用。
	 * Inactive: 在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径。
	 * SwapTotal: 交换空间的总大小。
	 * SwapFree: 未被使用交换空间的大小。
	 * Dirty: 等待被写回到磁盘的内存大小。
	 * Writeback: 正在被写回到磁盘的内存大小。
	 * AnonPages：未映射页的内存大小。
	 * Mapped: 设备和文件等映射的大小。
	 * Slab: 内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗。
	 * SReclaimable:可收回Slab的大小。
	 * SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）。
	 * PageTables：管理内存分页页面的索引表的大小。
	 * NFS_Unstable:不稳定页表的大小。
	 *
	 * @return
	 */
	public String getDeviceTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Long.valueOf(arrayOfString[1]) * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB或GB，内存大小规格化
	}

	/**
	 * 获取应用内存信息
	 *
	 * 不需要 任何 权限
	 *
	 * Android 11 测试可行
	 */
	public void getAppMemory() {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//最大分配内存
		int memory = activityManager.getMemoryClass();
		Log.d(TAG, "app memory: "+memory);
		//最大分配内存获取方法2（单位Byte）
//		float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0/ (1024 * 1024));
		// 格式化为KB/MB/GB
		String maxMemory = Formatter.formatFileSize(context, Runtime.getRuntime().maxMemory());
		//当前分配的总内存（单位Byte）
//		float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));
		String totalMemory = Formatter.formatFileSize(context, Runtime.getRuntime().totalMemory());
		//剩余内存（单位Byte）
//		float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0/ (1024 * 1024));
		String freeMemory = Formatter.formatFileSize(context, Runtime.getRuntime().freeMemory());
		Log.d(TAG, "app maxMemory: "+maxMemory);
		Log.d(TAG, "app totalMemory: "+totalMemory);
		Log.d(TAG, "app freeMemory: "+freeMemory);
	}

	/**
	 *
	 * 获取cpu事情率
	 *
	 * 测试不可行（高版本系统已经禁止执行该命令）
	 */
	public void getCpuUsage() {
		try {
			String Result;
			Process p=Runtime.getRuntime().exec("top -n 1");

			BufferedReader br=new BufferedReader(new InputStreamReader
					(p.getInputStream ()));
			while((Result=br.readLine())!=null)
			{
				if(Result.trim().length()<1){
					continue;
				}else{
					StringBuffer tv = new StringBuffer();
					String[] CPUusr = Result.split("%");
					tv.append("USER:"+CPUusr[0]+"\n");
					String[] CPUusage = CPUusr[0].split("User");
					String[] SYSusage = CPUusr[1].split("System");
					tv.append("CPU:"+CPUusage[1].trim()+" length:"+CPUusage[1].trim().length()+"\n");
					tv.append("SYS:"+SYSusage[1].trim()+" length:"+SYSusage[1].trim().length()+"\n");
					tv.append(Result+"\n");
					Log.d(TAG, "cpu usage: " + tv);
					break;
				}
			}
		} catch (Throwable t) {
			MobLog.getInstance().w(t);
		}
	}

	/**
	 *
	 * 获取cpu使用率
	 *
	 * 8.0开始不可行
	 *
	 * @return
	 */
	public float getProcessCpuRate()
	{

		float totalCpuTime1 = getTotalCpuTime();
		float processCpuTime1 = getAppCpuTime();
		try
		{
			Thread.sleep(360);  //sleep一段时间
		}
		catch (Exception e)
		{
		}

		float totalCpuTime2 = getTotalCpuTime();
		float processCpuTime2 = getAppCpuTime();

		float cpuRate = 100 * (processCpuTime2 - processCpuTime1) / (totalCpuTime2 - totalCpuTime1);//百分比

		return cpuRate;
	}

	/**
	 *
	 * 8.0开始不可用：FileNotFoundException（Permission Denied）
	 *
	 * @return
	 */
	// 获取系统总CPU使用时间
	public long getTotalCpuTime()
	{
		long totalCpu = -1;
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			String[] cpuInfos = load.split(" ");
			totalCpu = Long.parseLong(cpuInfos[2])
					+ Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
					+ Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
					+ Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return totalCpu;
	}

	/**
	 * 获取应用占用的CPU时间
	 *
	 * 8.0开始不可行
	 */
	public long getAppCpuTime()
	{
		String[] cpuInfos = null;
		try
		{
			int pid = android.os.Process.myPid();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/" + pid + "/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		long appCpuTime = Long.parseLong(cpuInfos[13])
				+ Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
				+ Long.parseLong(cpuInfos[16]);
		return appCpuTime;
	}

	/**
	 *
	 * 获取cpu使用率
	 *
	 * 8.0开始不允许读取/proc/stat文件
	 *
	 * https://www.jianshu.com/p/6bf564f7cdf0
	 * https://blog.csdn.net/aahuangjianjun/article/details/82622350
	 *
	 * 通过获取cpu一行的数据，即可进行CPU占用率的计算。我们会用到的数据有:
	 - user(21441),从系统启动开始累计到当前时刻，处于用户态的运行时间，不包含nice值为负的进程。
	 - nice(3634),从系统启动开始累计到当前时刻，nice值为负的进程所占用的CPU时间。
	 - system(13602),从系统启动开始累计到当前时刻，处于核心态的运行时间。
	 - idle(818350),从系统启动开始累计到当前时刻，除IO等待时间以外的其它等待时间。
	 - iowait(3535),从系统启动开始累计到当前时刻，IO等待时间。
	 - irq(2),从系统启动开始累计到当前时刻，硬中断时间。
	 - softirq(99),从系统启动开始累计到当前时刻，软中断时间。
	 总的CPU占用率的计算方法为：采样两个足够短的时间间隔的CPU快照，
	 CPU占用率 = 100*((totalTime2-totalTime1)-(idle2-idle1))/(totalTime2-totalTime1)。
	 * */
	public float getCpuRate(){
		float cpuRate = -1;
		try {
			//采样第一次cpu信息快照
			Map<String,String> map1 = getMap();
			//总的CPU时间totalTime = user+nice+system+idle+iowait+irq+softirq
			long totalTime1 =getTime(map1);
			System.out.println(totalTime1+"...........................totalTime1.");
			//获取idleTime1
			long idleTime1 = Long.parseLong(map1.get("idle"));
			System.out.println(idleTime1 + "...................idleTime1");
			//间隔360ms
			try {
				Thread.sleep(360);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//采样第二次cpu信息快照
			Map<String,String> map2 = getMap();
			long totalTime2 = getTime(map2);
			System.out.println(totalTime2+"............................totalTime2");
			//获取idleTime1
			long idleTime2 = Long.parseLong(map2.get("idle"));
			System.out.println(idleTime2+"................idleTime2");

			//得到cpu的使用率
			cpuRate = 100*((totalTime2-totalTime1)-(idleTime2-idleTime1))/(totalTime2-totalTime1);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return cpuRate;
	}

	//得到cpu信息
	public long getTime(Map<String,String> map){
		long totalTime = -1;
		try {
			if (map != null && !map.isEmpty()) {
				totalTime = Long.parseLong(map.get("user")) + Long.parseLong(map.get("nice"))
						+ Long.parseLong(map.get("system")) + Long.parseLong(map.get("idle"))
						+ Long.parseLong(map.get("iowait")) + Long.parseLong(map.get("irq"))
						+ Long.parseLong(map.get("softirq"));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return totalTime;
	}

	/**
	 * 采样CPU信息快照的函数，返回Map类型
	 *
	 * 测试失败：Permission Denied，8.0开始不允许读取/proc/stat文件
	 * @return
	 */
	public Map<String,String> getMap(){
		String[] cpuInfos = null;
		//读取cpu信息文件
		BufferedReader br = null;
		Map<String,String> map = new HashMap<>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")));
			String load = br.readLine();
			br.close();
			cpuInfos = load.split(" ");
			if (cpuInfos != null && cpuInfos.length > 0) {
				map.put("user",cpuInfos[2]);
				map.put("nice",cpuInfos[3]);
				map.put("system",cpuInfos[4]);
				map.put("idle",cpuInfos[5]);
				map.put("iowait",cpuInfos[6]);
				map.put("irq",cpuInfos[7]);
				map.put("softirq",cpuInfos[8]);
			}
		} catch (FileNotFoundException e) {
			System.out.println("文件未找到");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("线程异常");
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 判断CPU位数（32/64）
	 *
	 * 不需要 任何 权限
	 *
	 * Android 11 测试可行
	 *
	 * @return
	 */
	public boolean isCPU64(){
		boolean result = false;
		String mProcessor = null;
		List<String > list = null;
		try {
			mProcessor = getFieldFromCpuinfo("Processor");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (mProcessor != null) {
			// D/CpuUtils: isCPU64 mProcessor = AArch64 Processor rev 4 (aarch64)
			Log.d(TAG, "isCPU64 mProcessor = " + mProcessor);
			//list =  Arrays.asList(mProcessor.split("\\s"));
			if (mProcessor.contains("aarch64")) {
				result = true;
			}
		}

		return result;
	}


	/*  cat /proc/cpuinfo
		processor       : 0
		Processor       : AArch64 Processor rev 4 (aarch64)
		model name      : AArch64 Processor rev 4 (aarch64)
		BogoMIPS        : 26.00
		Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
		CPU implementer : 0x41
		CPU architecture: 8
		CPU variant     : 0x0
		CPU part        : 0xd03
		CPU revision    : 4
	*/
	private String getFieldFromCpuinfo(String field) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));
		Pattern p = Pattern.compile(field + "\\s*:\\s*(.*)");

		try {
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					return m.group(1);
				}
			}
		} finally {
			br.close();
		}

		return null;
	}
}
