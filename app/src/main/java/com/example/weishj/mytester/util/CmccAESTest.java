package com.example.weishj.mytester.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by weishj on 2018/6/13.
 */

public class CmccAESTest {
	public static String AESEncode(String privateKey, String original) {
		try {
			SecretKeySpec var2 = new SecretKeySpec(privateKey.getBytes(), "AES");
			Cipher var3 = Cipher.getInstance("AES/CBC/PKCS5Padding");
			var3.init(1, var2, new IvParameterSpec(new byte[var3.getBlockSize()]));
			byte[] var4 = var3.doFinal(original.getBytes("UTF-8"));
			return b.a(var4);
		} catch (Exception var5) {
			var5.printStackTrace();
			return null;
		}
	}

	public static String AESDecode(String privateKey, String crypted) {
		try {
			byte[] var2 = b.a(crypted);
			SecretKeySpec var3 = new SecretKeySpec(privateKey.getBytes(), "AES");
			Cipher var4 = Cipher.getInstance("AES/CBC/PKCS5Padding");
			var4.init(2, var3, new IvParameterSpec(new byte[var4.getBlockSize()]));
			byte[] var5 = var4.doFinal(var2);
			return new String(var5, "UTF-8");
		} catch (Exception var6) {
			var6.printStackTrace();
			return null;
		}
	}
}
