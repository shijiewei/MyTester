package com.example.weishj.mytester.ui;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;
import com.example.weishj.mytester.util.NetTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RouterActivity extends BaseActivity {
	private TextView ipTv;
	private TextView macTv;
	private TextView wlanTv;
	private TextView routerTv;
	private byte[] ipByte;
	private int ipInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_router);

		initView();
		getRouterInfo();
//		getIPs();
		NetTool nt = new NetTool(this.getApplicationContext());
		nt.scan();
	}

	private void initView() {
		ipTv = (TextView) findViewById(R.id.router_ip_tv);
		HashMap<String, String> ips = getIps();
		if (ips != null && !ips.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : ips.entrySet()) {
				sb.append(entry.getKey());
				sb.append(": ");
				sb.append(entry.getValue());
				sb.append("\n");
				sb.append("------------------");
				sb.append("\n");
			}
			ipTv.setText(sb.toString());
		}

		macTv = (TextView) findViewById(R.id.router_mac_tv);
		HashMap<String, byte[]> macs = getMacs();
		if (macs != null && !macs.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, byte[]> entry : macs.entrySet()) {
				sb.append(entry.getKey());
				sb.append(": ");
				sb.append(getMacStr(entry.getValue()));
				sb.append("\n");
			}
			macTv.setText(sb.toString());
		}
		wlanTv = (TextView) findViewById(R.id.router_wlan_tv);
		wlanTv.setText(getWLAN0Info());
		routerTv = (TextView) findViewById(R.id.router_router_tv);
		routerTv.setText(getRouterInfo());
	}

	private String getMacStr(byte[] bMac) {
		if (bMac != null && bMac.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bMac.length; i ++) {
				String str = Integer.toHexString(bMac[i] & 0xFF).toUpperCase();
				if (str.length() < 2) {
					str = "0" + str;
				}
				sb.append(str);
				if (i < bMac.length - 1) {
					sb.append(":");
				}
			}
			return sb.toString();
		} else {
			return "                                ";
		}
	}

	private HashMap<String, String> getIps() {
		HashMap<String, String> ips = new HashMap<>();
		try {
			Iterator<NetworkInterface> obj = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
			StringBuilder ipSb = new StringBuilder();
			while (obj.hasNext()) {
				NetworkInterface nwInterface = obj.next();
				Iterator<InetAddress> iNetAddressIt = Collections.list(nwInterface.getInetAddresses()).iterator();
				while (iNetAddressIt.hasNext()) {
					InetAddress iNetAddress = iNetAddressIt.next();
					if (iNetAddress != null && iNetAddress instanceof Inet4Address && !TextUtils.isEmpty(iNetAddress.getHostAddress())) {
						ipSb.append(iNetAddress.getHostAddress());
						ipSb.append("\n");
					}
				}
				String ip;
				if (ipSb.toString().endsWith("\n")) {
					ip = ipSb.substring(0, ipSb.length() - 1);
				} else {
					ip = ipSb.toString();
				}
				ips.put(nwInterface.getName(), ip);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return ips;
	}

	private HashMap<String, byte[]> getMacs() {
		HashMap<String, byte[]> macs = new HashMap<>();
		try {
			Iterator<NetworkInterface> obj = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
			while (obj.hasNext()) {
				NetworkInterface nwInterface = obj.next();
//				if (nwInterface.getName().equalsIgnoreCase("wlan0")) {
//					return nwInterface.getHardwareAddress();
//				}
				macs.put(nwInterface.getName(), nwInterface.getHardwareAddress());
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return macs;
	}

	private String getWLAN0Info() {
		StringBuffer sb = new StringBuffer();
		try {
			Iterator<NetworkInterface> obj = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
			while (obj.hasNext()) {
				NetworkInterface nwInterface = obj.next();
				if (nwInterface.getName().equalsIgnoreCase("wlan0")) {
					sb.append("name: ");
					sb.append(nwInterface.getName());
					sb.append("\n");

					// 获取mac
					sb.append("mac: ");
					sb.append(getMacStr(nwInterface.getHardwareAddress()));
					sb.append("\n");

					// 获取InterfaceAddress
					List<InterfaceAddress> addressList = nwInterface.getInterfaceAddresses();
					if (addressList != null && !addressList.isEmpty()) {
						for (int i = 0; i < addressList.size(); i ++) {
							InterfaceAddress address = addressList.get(i);
							if (address != null) {
								// 只有IPV4有broadcast，IPV6时getBroadcast()会返回null
								if (address.getAddress() instanceof Inet6Address) {
									sb.append("IPV6: ");
									sb.append(address.getAddress().getHostAddress());
									sb.append("\n");
								}
								if (address.getAddress() instanceof Inet4Address) {
									sb.append("IPV4: ");
									sb.append(address.getAddress().getHostAddress());
									sb.append("\n");
									ipByte = address.getAddress().getAddress();
									ipInt = bytes2int(ipByte);
									sb.append("IPInt: ");
									sb.append(ipInt);
									sb.append("\n");
									if (address.getBroadcast() != null) {
										sb.append("broadcast: ");
										sb.append(address.getBroadcast().getHostAddress());
										sb.append("\n");
									}
								}
								sb.append("netMask: ");
								sb.append(calcNetMaskByPrefixLength(address.getNetworkPrefixLength()));
								sb.append("\n");
							}
						}
					}
					return sb.toString();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getRouterInfo() {
		WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
		DhcpInfo di = wm.getDhcpInfo();
		Log.d("jackie", "DhcpInfo: " + di.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("Gateway: ");
		sb.append(int2Ip(di.gateway));
		sb.append("\n");
		sb.append("NetMask: ");
		sb.append(calcNetMaskByPrefixLength(di.netmask));
		sb.append("\n");
		sb.append("DNS1: ");
		sb.append(int2Ip(di.dns1));
		sb.append("\n");
		sb.append("DNS2: ");
		sb.append(int2Ip(di.dns2));
		sb.append("\n");
		sb.append("IP: ");
		sb.append(int2Ip(di.ipAddress));
		sb.append("\n");
		sb.append("ServerAddress: ");
		sb.append(int2Ip(di.serverAddress));
		sb.append("\n");
		sb.append("BSSID(Wifi Mac): ");
		sb.append(getWifiMac());
		sb.append("\n");
		sb.append("SSID(Wifi Name): ");
		sb.append(getWifiName());
		return sb.toString();
	}

	private String getRouterMacFromIp(int ip) {
		String mac;
		// 方法一
		mac = getRouterMacFromIp1(ip);
		Log.d("jackie", "router mac: " + mac);
		return mac;
	}

	/**
	 * 通过IP获取MAC，可以获取接入到局域网中的设备mac，但无法根据网关获取路由器mac
	 *
	 * @param ip
	 * @return
	 */
	private String getRouterMacFromIp1(int ip) {
		try {
			// 通过ip获得IPVX实例
			InetAddress ipv4 = InetAddress.getByAddress(int2Bytes(ip));
			Log.d("jackie", "ipInt: " + ip + ", ipString: " + ipv4.getHostAddress());
			NetworkInterface nwInterface = NetworkInterface.getByInetAddress(ipv4);
			return getMacStr(nwInterface.getHardwareAddress());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取Wifi网络Mac
	 */
	private String getWifiMac() {
		WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
		Log.d("jackie", "WifiInfo: " + wifi.getConnectionInfo().toString());
		return wifi.getConnectionInfo().getBSSID();
	}
	/**
	 * 获取wifi名字
	 */
	private String getWifiName() {
		WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
		return wifi.getConnectionInfo().getSSID();
	}

	/**
	 * 获取ip
	 */
	private String getIp() {
		// 方法一
		WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
		return int2Ip(wifi.getConnectionInfo().getIpAddress());
		// 方法二：使用JDK中的NetworkInterface，获取name为“wlan0”的节点的ip，见getWLAN0Info()
		// 方法三：
//		WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
//		DhcpInfo di = wm.getDhcpInfo();
//		return int2Ip(di.ipAddress);
	}

	private byte[] int2Bytes(int num) {
//		byte[] bytes = new byte[4];
//		bytes[0] = (byte) (i & 0xff);
//		bytes[1] = (byte) ((i >> 8) & 0xff);
//		bytes[2] = (byte) ((i >> 16) & 0xff);
//		bytes[3] = (byte) ((i >> 24) & 0xff);
//		return bytes;
		byte[] result = new byte[4];
		result[0] = (byte)((num >>> 24) & 0xff);
		result[1] = (byte)((num >>> 16)& 0xff );
		result[2] = (byte)((num >>> 8) & 0xff );
		result[3] = (byte)((num >>> 0) & 0xff );
		return result;
	}

	public int bytes2int(byte[] bytes){
		int result = 0;
		if(bytes.length == 4){
			int a = (bytes[0] & 0xff) << 24;
			int b = (bytes[1] & 0xff) << 16;
			int c = (bytes[2] & 0xff) << 8;
			int d = (bytes[3] & 0xff);
			result = a | b | c | d;
		}
		return result;
	}

	private String int2Ip(int ip) {
		StringBuffer sb=new StringBuffer();
		sb.append(String.valueOf(ip & 0xff));
		sb.append('.');
		sb.append(String.valueOf((ip>>8) & 0xff));
		sb.append('.');
		sb.append(String.valueOf((ip>>16) & 0xff));
		sb.append('.');
		sb.append(String.valueOf((ip>>24) & 0xff));
		return sb.toString();
	}

	private String calcNetMaskByPrefixLength(int length) {
		int mask = -1 << (32 - length);
		int partsNum = 4;
		int bitsOfPart = 8;
		int maskParts[] = new int[partsNum];
		int selector = 0x000000ff;
		for (int i = 0; i < maskParts.length; i++) {
			int pos = maskParts.length - 1 - i;
			maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
		}
		String result = "";
		result = result + maskParts[0];
		for (int i = 1; i < maskParts.length; i++) {
			result = result + "." + maskParts[i];
		}
		return result;
	}

	/**
	 * 安卓设备无法使用arp命令获取ip
	 *
	 * @return
	 */
	public static List<String>  getIPs()
	{
		List<String> list = new ArrayList<String>();
		boolean flag = false;
		int count=0;
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec("arp -a");
			BufferedReader br = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String inline;
			while ((inline = br.readLine()) != null) {
				if(inline.indexOf("接口") > -1){
					flag = !flag;
					if(!flag){
						//碰到下一个"接口"退出循环
						break;
					}
				}
				if(flag){
					count++;
					if(count > 2){
						//有效IP
						String[] str=inline.split(" {4}");
						list.add(str[0]);
					}
				}
				Log.d("jackie", "inline: " + inline);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("jackie", "list: " + list);
		return list;
	}
}
