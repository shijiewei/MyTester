package com.example.weishj.mytester.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by weishj on 2018/6/13.
 */

public class b {
	private static final char[] a = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

	public static String a(byte[] var0) {
		byte var1 = 0;
		int var2 = var0.length;
		StringBuffer var3 = new StringBuffer(var0.length * 3 / 2);
		int var4 = var2 - 3;
		int var5 = var1;
		int var6 = 0;

		int var7;
		while(var5 <= var4) {
			var7 = (var0[var5] & 255) << 16 | (var0[var5 + 1] & 255) << 8 | var0[var5 + 2] & 255;
			var3.append(a[var7 >> 18 & 63]);
			var3.append(a[var7 >> 12 & 63]);
			var3.append(a[var7 >> 6 & 63]);
			var3.append(a[var7 & 63]);
			var5 += 3;
			if(var6++ >= 14) {
				var6 = 0;
				var3.append(" ");
			}
		}

		if(var5 == var1 + var2 - 2) {
			var7 = (var0[var5] & 255) << 16 | (var0[var5 + 1] & 255) << 8;
			var3.append(a[var7 >> 18 & 63]);
			var3.append(a[var7 >> 12 & 63]);
			var3.append(a[var7 >> 6 & 63]);
			var3.append("=");
		} else if(var5 == var1 + var2 - 1) {
			var7 = (var0[var5] & 255) << 16;
			var3.append(a[var7 >> 18 & 63]);
			var3.append(a[var7 >> 12 & 63]);
			var3.append("==");
		}

		return var3.toString();
	}

	private static int a(char var0) {
		if(var0 >= 65 && var0 <= 90) {
			return var0 - 65;
		} else if(var0 >= 97 && var0 <= 122) {
			return var0 - 97 + 26;
		} else if(var0 >= 48 && var0 <= 57) {
			return var0 - 48 + 26 + 26;
		} else {
			switch(var0) {
				case '+':
					return 62;
				case '/':
					return 63;
				case '=':
					return 0;
				default:
					throw new RuntimeException("unexpected code: " + var0);
			}
		}
	}

	public static byte[] a(String var0) {
		ByteArrayOutputStream var1 = new ByteArrayOutputStream();

		try {
			a(var0, var1);
		} catch (IOException var5) {
			throw new RuntimeException();
		}

		byte[] var2 = var1.toByteArray();

		try {
			var1.close();
			var1 = null;
		} catch (IOException var4) {
			System.err.println("Error while decoding BASE64: " + var4.toString());
		}

		return var2;
	}

	private static void a(String var0, OutputStream var1) throws IOException {
		int var2 = 0;
		int var3 = var0.length();

		while(true) {
			while(var2 < var3 && var0.charAt(var2) <= 32) {
				++var2;
			}

			if(var2 == var3) {
				break;
			}

			int var4 = (a(var0.charAt(var2)) << 18) + (a(var0.charAt(var2 + 1)) << 12) + (a(var0.charAt(var2 + 2)) << 6) + a(var0.charAt(var2 + 3));
			var1.write(var4 >> 16 & 255);
			if(var0.charAt(var2 + 2) == 61) {
				break;
			}

			var1.write(var4 >> 8 & 255);
			if(var0.charAt(var2 + 3) == 61) {
				break;
			}

			var1.write(var4 & 255);
			var2 += 4;
		}

	}
}
