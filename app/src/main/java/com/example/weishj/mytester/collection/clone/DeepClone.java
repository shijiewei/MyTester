package com.example.weishj.mytester.collection.clone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by weishj on 2018/4/11.
 */

public class DeepClone {
	/**
	 * 通过流实现包含对象引用的HashMap的深拷贝，其中的对象必须是可序列化的（实现Serializable接口）
	 *
	 * @param obj
	 * @return
	 */
	public static Object deepClone(Object obj) {
		try {// 将对象写到流里
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			oo.close();
			// 从流里读出来
			ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
			ObjectInputStream oi = new ObjectInputStream(bi);
			Object o = oi.readObject();
			oi.close();
			return o;
		} catch (Exception e) {
			return null;
		}
	}
}
