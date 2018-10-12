package com.example.weishj.mytester.singleton;

import com.example.weishj.mytester.crypto.Crypto;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.Hashon;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by weishj on 2018/4/9.
 */

public class SingleTon {
	private byte[] aes = null;
	private Hashon hashon;
	private SingleTon() {
		try {
			aes = Crypto.generateAESKey();
			hashon = new Hashon();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public static SingleTon getInstance() {
		return Holder.INSTANCE;
	}

	public void encodePhone(HashMap<String, Object> tmp) {
		String aes = "1234567809876543";
		HashMap<String, Object> map = (HashMap<String, Object>)tmp.get("key");
		ArrayList<HashMap<String, Object>> contacts = (ArrayList<HashMap<String, Object>>)map.get("contacts");
		try {
			for (HashMap<String, Object> contact : contacts) {
				ArrayList<HashMap<String, Object>> phones = (ArrayList<HashMap<String, Object>>)contact.get("phones");
				for (HashMap<String, Object> phone : phones) {
					String tmpPhone = (String)phone.get("phone");
					phone.put("phone", Crypto.byteToHex(Data.AES128Encode(aes, tmpPhone)));
				}
			}

		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		String json = hashon.fromHashMap(map);
		System.out.println("jackie====> TID: " + Thread.currentThread().getId() + ", Data: " + json);
	}

	private static class Holder {
		static SingleTon INSTANCE = new SingleTon();
	}
}
