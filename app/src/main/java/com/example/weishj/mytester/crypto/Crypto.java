package com.example.weishj.mytester.crypto;

import android.text.TextUtils;

import com.mob.tools.utils.Data;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.MobRSA;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by weishj on 2017/5/3.
 */

public class Crypto {
	private static final String SMRSA_PUBLICK_KEY_DEFUALT = "fa3acdf1b118fc26668bf72a70d60aa024a2667" +
			"254c5f0bb8f082bc384b38a4e6d3d1b672467a19793c8f770c63f48b409e87f5787371789af40b95eae9867b9";
	private static final String SMRSA_MODULUS_KEY_DEFUALT = "1ef570e1013109c50df8f8c2015faed71e4cf7c" +
			"53ca9195a99c574ca046aeefdf70bc5fd69f04b0eadf63398698f776cf1ef0db5134efddc3aa4825b69aee9" +
			"4b55356a15d2a50a325ef7bd2d9efe15f3ac5d2303e0bdf5147b3d0fb5fa4fd1d5ea07fe1b45912ff9d7fe4" +
			"72136ff49cb1176f039219bc737ec7ccad132a5ce57";

	private static final int SMRSA_KEY_SIZE_DEFUALT = 1024;
	private static String rsaPublicKey = SMRSA_PUBLICK_KEY_DEFUALT;
	private static String rsaModulusKey = SMRSA_MODULUS_KEY_DEFUALT;
	private static int rsaKeySize = SMRSA_KEY_SIZE_DEFUALT;

	public static String genAESKey() throws Throwable {
		KeyGenerator kg = KeyGenerator.getInstance("AES");
		kg.init(128);
		SecretKey sk = kg.generateKey();
		byte[] b = sk.getEncoded();
		String s = Data.byteToHex(b);
		return s;
	}

	public static byte[] generateAESKey() throws Throwable {
		Random rnd = new Random();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeLong(rnd.nextLong());
		dos.writeLong(rnd.nextLong());
		dos.flush();
		dos.close();
		byte[] aesKey = baos.toByteArray();
		return aesKey;
	}

	public static String restoreAES(byte[] bAes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bAes);
		DataInputStream dis = new DataInputStream(bais);
//		StringBuffer lineSB = new StringBuffer();
//		try {
//			lineSB.append(dis.readLong());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		String lineS = null;
		try {
			lineS = String.valueOf(dis.readLong());
		} catch (IOException e) {
			e.printStackTrace();
		}
//		InputStreamReader isr = new InputStreamReader(bais);
//		BufferedReader br = new BufferedReader(isr);
//		String lineBR = null;
//		try {
//			lineBR = br.readLine();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("lineBR = " + lineBR);
//		System.out.println("lineSB = " + lineSB);
//		System.out.println("lineS = " + lineS);
		return lineS.toString();
	}

	public static byte[] encodeData(String json, boolean zip, String aeskey) throws Throwable {
		byte[] data = json.getBytes();
		if (zip) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gzos = new GZIPOutputStream(baos);
			gzos.write(data);
			gzos.close();
			data = baos.toByteArray();
		}

		if (TextUtils.isEmpty(aeskey)){
			return null;
		}
		byte[] bKey = Data.rawMD5(aeskey.getBytes());
		data = Data.AES128Encode(bKey, data);
		return data;
	}

	public static String decodeData(byte[] data,String aeskey) throws Throwable {
		byte[] rawData;
		if (TextUtils.isEmpty(aeskey)) {
			return null;
		}
		byte[] bKey = Data.rawMD5(aeskey.getBytes());
		rawData = Data.AES128Decode(bKey, data);

		String resp = new String(rawData, "utf-8");
		if (TextUtils.isEmpty(resp)) {
			throw new Throwable("[decode]Response is empty");
		}

		return resp.trim();
	}

	public static byte[] SMRSADecode(byte[] source) throws Throwable {
		BigInteger bigPublic = new BigInteger(rsaPublicKey,16);
		BigInteger bigModules = new BigInteger(rsaModulusKey,16);

		MobRSA rsa = new MobRSA(rsaKeySize);
		return rsa.decode(source, bigPublic, bigModules);
	}

	public static byte[] SMRSAEncode(byte[] source) throws Throwable {
		BigInteger bigPublic = new BigInteger(rsaPublicKey,16);
		BigInteger bigModules = new BigInteger(rsaModulusKey,16);

		MobRSA rsa = new MobRSA(rsaKeySize);
		return rsa.encode(source,bigPublic,bigModules);
	}

	public static byte[] hexToByte(String hexData){
		if (hexData == null) {
			return null;
		}
		int len = hexData.length();
		if (len % 2 == 1) {
			return null;
		}
		int dataLength = len / 2;
		byte[] result = new byte[dataLength];
		try {
			for (int i = 0; i < dataLength; i++) {
				result[i] = (byte) Integer.parseInt(hexData.substring(i * 2, i * 2 + 2), 16);
			}
		} catch (Throwable e) {
			return null;
		}
		return result;
	}

	public static String byteToHex(byte[] byteData){
		return Data.byteToHex(byteData);
	}

}
