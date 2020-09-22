package com.example.weishj.mytester.crypto;

import com.mob.tools.utils.Data;

import java.security.MessageDigest;

public class MD5Test {
	public static final String[] ARR =  {"d21142dfb0d16fb", "151cf58bwy2bad46", "1c91672fk8cbac73b"};
	public static void main(String[] args) {
		for (String str : ARR) {
			md5(str);
		}
		convert2Md5(ARR);
	}

	private static void convert2Md5(String[] origin) {
		if (origin != null) {
			String rst = "";
			int size = origin.length;
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					rst += "[";
				}
				rst += "\"" + Data.byteToHex(Data.rawMD5(origin[i])) + "\"";
				if (i < size - 1) {
					rst += ", ";
				}
				if (i == size - 1) {
					rst += "]";
				}
			}
			System.out.println("convert to md5: " + rst);
		}
	}

	private static void md5(String origin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(origin.getBytes());// 将original传给dumd5
			byte[] digist = md.digest();// 产生md5序列
			StringBuffer sb = new StringBuffer();// 转换zhimd5值为16进制dao
			for (byte b : digist) {
				sb.append(String.format("%02x", b & 0xff));
			}
			System.out.println("md5-1: " + sb);
			System.out.println("md5-2: " + Data.byteToHex(Data.rawMD5(origin)));
		} catch (Throwable t) {

		}
	}
}
