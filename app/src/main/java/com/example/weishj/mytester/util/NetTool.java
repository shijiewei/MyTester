package com.example.weishj.mytester.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by weishj on 2018/6/6.
 *
 * 通过循环ping本机ip网段内的所有256个ip，以查找当前局域网中所有主机ip
 *
 */
public class NetTool {
	private int SERVERPORT = 8888;
	private String locAddress;//存储本机ip，例：本地ip ：192.168.1.1
	private String locAddressPref;//存储本机ip前缀，例：本地ip ：192.168.1.
	private Runtime run = Runtime.getRuntime();//获取当前运行环境，来执行ping，相当于windows的cmd
	private Process proc = null;
	private String ping = "ping -c 1 -w 0.5 " ;//其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
	private int j;//存放ip最后一位地址 0-255
	private Context ctx;//上下文

	public NetTool(Context ctx){
		this.ctx = ctx;
	}

	private Handler handler = new Handler(){
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
				case 222:// 服务器消息
					break;
				case 333:// 扫描完毕消息
					Toast.makeText(ctx, "扫描到主机："+((String)msg.obj).substring(6), Toast.LENGTH_LONG).show();
					break;
				case 444://扫描失败
					Toast.makeText(ctx, (String)msg.obj, Toast.LENGTH_LONG).show();
					break;
			}
		}

	};

	//向serversocket发送消息
	public String sendMsg(String ip,String msg) {

		String res = null;
		Socket socket = null;

		try {
			socket = new Socket(ip, SERVERPORT);
			socket.setSoTimeout(500);
			//向服务器发送消息
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			os.println(msg);
			os.flush();// 刷新输出流，使Server马上收到该字符串

			//从服务器获取返回消息
			DataInputStream input = new DataInputStream(socket.getInputStream());
			res = input.readUTF();
			Log.d("jackie", "server 返回信息：" + res);
			Message.obtain(handler, 222, res).sendToTarget();//发送服务器返回消息

		} catch (Exception unknownHost) {
			Log.w("jackie", "You are trying to connect to an unknown host. ip: " + ip);
		} finally {
			// 4: Closing connection
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		return res;
	}



	/**
	 * 扫描局域网内ip，找到对应服务器
	 */
	public void scan(){
		locAddressPref = getLocAddrPref();//获取本地ip前缀
		if(locAddressPref.equals("")){
			Toast.makeText(ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
			return ;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				for ( int i = 0; i < 256; i++) {
					j = i ;
					String p = NetTool.this.ping + locAddressPref + NetTool.this.j ;
					String current_ip = locAddressPref + NetTool.this.j;
					try {
						proc = run.exec(p);
						if (proc != null) {
							BufferedReader successReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
							String line;
							while ((line = successReader.readLine()) != null) {
								Log.i("jackie", line);
							}
							int result = proc.waitFor();
							if (result == 0) {
								Log.d("jackie", "连接成功: " + current_ip);
								// 向服务器发送验证信息
								String msg = sendMsg(current_ip, "scan "+ locAddress+" ( "+android.os.Build.MODEL+" ) ");

								//如果验证通过...
								if (msg != null){
									if (msg.contains("OK")){
										Log.d("jackie", "服务器IP：" + msg.substring(8,msg.length()));
										Message.obtain(handler, 333, msg.substring(2,msg.length())).sendToTarget();//返回扫描完毕消息
									}
								}
							} else {
//							Log.d("jackie", "Process exits un-normal");
							}
						} else {
							Log.e("jackie", "ping fail:proc is null.");
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					} finally {
						proc.destroy();
					}
				}
			}
		}).start();
	}


	//获取本地ip地址
	public String getLocAddress(){
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface networks = en.nextElement();
				// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> address = networks.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (address.hasMoreElements()) {
					InetAddress ip = address.nextElement();
					if (!ip.isLoopbackAddress()
							&& ip instanceof Inet4Address) {
						ipaddress = ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			Log.e("jackie", "获取本地ip地址失败");
			e.printStackTrace();
		}
		Log.d("jackie", "本机IP:" + ipaddress);
		locAddress = ipaddress;
		return ipaddress;

	}

	//获取IP前缀
	public String getLocAddrPref(){
		String str = getLocAddress();
		if(!str.equals("")){
			return str.substring(0,str.lastIndexOf(".")+1);
		}
		return null;
	}

	//获取本机设备名称
	public String getLocDeviceName() {
		return android.os.Build.MODEL;
	}
}
