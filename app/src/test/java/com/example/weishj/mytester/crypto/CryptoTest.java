package com.example.weishj.mytester.crypto;

import android.util.Base64;

import com.example.weishj.mytester.JUnitBase;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.Hashon;

import junit.framework.Assert;

import org.junit.Test;

import java.security.Security;

/**
 * Created by weishj on 2017/5/3.
 */

public class CryptoTest extends JUnitBase {

	@Test
	public void test_genAESKey() throws Throwable {
		String s = Crypto.genAESKey();
		System.out.println(s);
		System.out.println("十六进制密钥长度为"+s.length());
		System.out.println("二进制密钥的长度为"+s.length()*4);
		Assert.assertEquals(32, s.length());
	}

	@Test
	public void testDecodeData() throws Throwable {
		String s = Crypto.genAESKey();
		String data = "asd";
		byte[] encoded = Crypto.encodeData(data, false, s);
		String decoded = Crypto.decodeData(encoded, s);
		Assert.assertEquals(data, decoded);
	}

	@Test
	public void testRSADecode() throws Throwable {
		String encodedAes = "000000801b1df5d9d6d7540dc9970cc83b255d8b7c724dd9ea484fb5c765c4998c50fef3a0e895d330ad78fb378b1ff63b5c40f0f627e75c781f28091fbba4bd4125437dfe34d03e139fbadf7ba76f14d5556f25ab751b45984ad9353f88c37578cea085a5b7e64f63aff90c597cf8f5eae552e32cd486a864ccc34017ee229f68b16b98";
		String expectedAes = "";
		String decoded = Crypto.byteToHex(Crypto.SMRSADecode(Crypto.hexToByte(encodedAes)));
//		String decoded = Crypto.byteToHex(Crypto.SMRSADecode(encodedAes.getBytes("UTF-8")));
		System.out.println("aes = " + decoded);
	}

	@Test
	public void testRSAEncode() throws Throwable {
		byte[] aes = Crypto.generateAESKey();
		System.out.println("aes = " + new String(aes, "utf-8"));
		System.out.println("aesString = " + Crypto.restoreAES(aes));
		System.out.println("aesHex = " + Crypto.byteToHex(aes));
		for (int i = 0; i < aes.length; i ++) {
			System.out.print(String.valueOf(aes[i]));
		}
		String encodedAes = Crypto.byteToHex(Crypto.SMRSAEncode(aes));

		System.out.println("\nencodedAes = " + encodedAes);

		String phone = "13123421234";

		String encodedPhone = Data.byteToHex(Data.AES128Encode(aes, phone));
		System.out.println("encodedPhone = " + encodedPhone);

//		String decodedAes = Crypto.byteToHex(Crypto.SMRSADecode(Crypto.SMRSAEncode(aes)));
//		System.out.println("decodedAes = " + decodedAes);

		String decodePhone = new String(Data.AES128Decode(aes, Crypto.hexToByte(encodedPhone)));
		System.out.println("decodePhone = " + decodePhone);
	}

	/**
	 * 奔奔的数据进行两次aes解密
	 * 结果：第二次解密失败，说明两次aes加密使用的密钥不同，第二次解不开
	 *
	 * @throws Throwable
	 */
	@Test
	public void testAESDecode() throws Throwable {
		String aesHex = "0c6390299f313ec1256e1a88fd5128a2";
		String phone = "4055201c63756ece17aadf1289050cba";
		System.out.println("phone = " + phone);
		String encodedPhone = "9fbfe3c74d40201d5f91a57514f01ab4ea4ff16f22f702c2d3a94682d80b3354a13128c8cbef23a9e712e5a877268f2f";
		String decodePhone = new String(Data.AES128Decode(Crypto.hexToByte(aesHex), Crypto.hexToByte(encodedPhone)), "utf-8").trim();
		System.out.println("decodePhone = " + decodePhone);

		String decodePhone2 = new String(Data.AES128Decode(Crypto.hexToByte(aesHex), Crypto.hexToByte(decodePhone)), "utf-8").trim();
		System.out.println("decodePhone2 = " + decodePhone2);
	}

	@Test
	public void testAESDecode2() throws Throwable {
		String aesHex = "c4e99910e1fe034cf40cec5426182c54";
		String encodedPhone = "598662dfd47dfee757d6aa3ba34a298f";
		byte[] decodedByte = Data.AES128Decode(Crypto.hexToByte(aesHex), Crypto.hexToByte(encodedPhone));
		String decodedStr = new String(decodedByte, "utf-8");
		String decodedHex = Crypto.byteToHex(decodedByte);
		System.out.println("decodedStr = " + decodedStr);
		System.out.println("decodedHex = " + decodedHex);
	}

	/**
	 * 验证手机号进行两次aes加密的情况
	 *
	 * @throws Throwable
	 */
	@Test
	public void testRSAEncode2() throws Throwable {
		String aesHex = "0c6390299f313ec1256e1a88fd5128a2";
		byte[] aes = Crypto.hexToByte(aesHex);
		String encodedAes = Crypto.byteToHex(Crypto.SMRSAEncode(aes));
		System.out.println("\nencodedAes = " + encodedAes);
		String phone = "13125847854";
		System.out.println("phone = " + phone);
		String encodedPhone = Crypto.byteToHex(Data.AES128Encode(aes, phone));
		System.out.println("encodedPhone = " + encodedPhone);
		String encodedPhone2 = Crypto.byteToHex(Data.AES128Encode(aes, encodedPhone));
		System.out.println("encodedPhone2 = " + encodedPhone2);

		String decodePhone = new String(Data.AES128Decode(aes, Crypto.hexToByte(encodedPhone2)), "utf-8").trim();
		System.out.println("decodePhone = " + decodePhone);
		String decodePhone2 = new String(Data.AES128Decode(aes, Crypto.hexToByte(decodePhone)), "utf-8").trim();
		System.out.println("decodePhone2 = " + decodePhone2);
	}

	/**
	 * 验证单例多线程导致两次aes加密的情况
	 *
	 * @throws Throwable
	 */
	@Test
	public void testSample() throws Throwable {
		String str = "v0";
		String aes = "1234567809876543";
		String expectedEncoded = "95a6c774957f229e92622e1997fb123b";
		String expectedEncoded2 = "f65c6a184dd1580673c59aa69fecfc3da087cc032b3b62306c6416ff8af963f34577ae93973ebec9e8799a0534d5dec1";
		String encoded = Crypto.byteToHex(Data.AES128Encode(aes, str));
		System.out.println("encoded = " + encoded);
		org.junit.Assert.assertEquals(expectedEncoded, encoded);

		String encoded2 = Crypto.byteToHex(Data.AES128Encode(aes, encoded));
		System.out.println("encoded2 = " + encoded2);
		org.junit.Assert.assertEquals(expectedEncoded2, encoded2);
	}
}
