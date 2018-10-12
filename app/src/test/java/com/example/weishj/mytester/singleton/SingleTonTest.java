package com.example.weishj.mytester.singleton;

import com.example.weishj.mytester.JUnitBase;
import com.example.weishj.mytester.collection.clone.DeepClone;
import com.mob.tools.utils.Hashon;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by weishj on 2018/4/9.
 */

public class SingleTonTest extends JUnitBase {
	@Test
	public void testSingleTon() throws Exception {
		String json = "{\"plat\":1,\"contacts\":[{\"displayname\":\"测试联系人\",\"phones\":[{\"phone\":\"13124321243\",\"type\":2}],\"lastname\":\"测试联系人\"},{\"firstname\":\"大\",\"displayname\":\"大红\",\"phones\":[{\"phone\":\"12343212348\",\"type\":2}],\"lastname\":\"红\"},{\"firstname\":\"杰\",\"displayname\":\"杰克\",\"phones\":[{\"phone\":\"13098452345\",\"type\":2}],\"lastname\":\"克\"},{\"firstname\":\"联\",\"displayname\":\"联想机\",\"phones\":[{\"phone\":\"13598709324\",\"type\":2}],\"company\":\"Yoozoo\",\"lastname\":\"想机\"}]}";
		// Hashon必须有Android context才能使用
		Hashon hashon = new Hashon();
		final HashMap<String, Object> map = hashon.fromJson(json);
		HashMap<String, Object> m1 = new HashMap<>();
		m1.put("key", DeepClone.deepClone(map));
//		m1.put("key", map);
		SingleTon.getInstance().encodePhone(m1);
		new Thread() {
			@Override
			public void run() {
				HashMap<String, Object> m2 = new HashMap<>();
				m2.put("key", DeepClone.deepClone(map));
//				m2.put("key", map);
				SingleTon.getInstance().encodePhone(m2);
			}
		}.start();


		String n = "254e167248575";
		BigDecimal bd1 = new BigDecimal(n);
		System.out.println(bd1.toPlainString());
		System.out.println(bd1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
	}
}
