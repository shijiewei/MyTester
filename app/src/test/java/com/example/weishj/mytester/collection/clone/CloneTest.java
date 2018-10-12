package com.example.weishj.mytester.collection.clone;

import com.example.weishj.mytester.JUnitBase;
import com.example.weishj.mytester.singleton.SingleTon;
import com.mob.tools.utils.Hashon;

import org.junit.Test;

import java.util.HashMap;

/**
 * Created by weishj on 2018/4/9.
 */

public class CloneTest extends JUnitBase {
	@Test
	public void testClone() throws Exception {
		Student zhangsan = new Student("zhangsan","男",25);
		HashMap<Integer,Object> map = new HashMap<Integer,Object>();
		map.put(1, zhangsan);
		// 方法1：clone()
//		HashMap<Integer,Object> cloneMap = (HashMap<Integer, Object>) map.clone();
		// 方法2：浅拷贝
//		HashMap<Integer,Object> cloneMap = map;
		// 方法3：效果同clone()一样
//		HashMap<Integer,Object> cloneMap = new HashMap<>();
//		cloneMap.putAll(map);
		// 方法4：深拷贝
		HashMap<Integer,Object> cloneMap = (HashMap<Integer,Object>)DeepClone.deepClone(map);
				System.out.println("*************************不做改变***********************************");
		System.out.println("未改变之前,     map的值:"+map.toString());
		System.out.println("未改变之前,cloneMap的值:"+cloneMap.toString());
		System.out.println("map和cloneMap是否指向同一内存地址:"+(map==cloneMap));
		System.out.println("map和cloneMap中存储的student是否指向同一内存地址:"+(map.get(1)==cloneMap.get(1)));
		//对cloneMap中的值进行改变，看是否能影响到map
		Student cloneLisi = (Student) cloneMap.get(1);
		cloneLisi.setSex("女");
		System.out.println("*************************对map中的值做出修改****************************");
		System.out.println("改变之后,cloneMap的值:"+cloneMap.toString());
		System.out.println("改变之后,     map的值:"+map.toString());

		System.out.println("*************************对map新增**********************************");
		Student lisi = new Student("lisi","男",18);
		map.put(2, lisi);
		System.out.println("改变之后,cloneMap的值:"+cloneMap.toString());
		System.out.println("改变之后,     map的值:"+map.toString());
	}

	@Test
	public void testObjectClone() throws Exception {
		Person p = new Person(23, "zhang");
		Person p1 = (Person) p.clone();
		System.out.println(p);
		System.out.println(p1);

		System.out.println("pName："+p.getName().hashCode());
		System.out.println("p1Name："+p1.getName().hashCode());
		System.out.println("p和p1是否指向同一个内存地址："+ (p1 == p));
		System.out.println("p.name和p1.name是否指向同一个内存地址："+ (p1.getName() == p.getName()));
	}

	@Test
	public void testObjectDeepClone() throws Exception {
		Body body = new Body(new Head(new Face()));
//		Body body1 = (Body) body.clone();
		Body body1 = (Body)DeepClone.deepClone(body);
		System.out.println("body == body1 : " + (body == body1) );
		System.out.println("body.head == body1.head : " +  (body.head == body1.head));
		System.out.println("body.head.face == body1.head.face : " +  (body.head.face == body1.head.face));
	}
}
