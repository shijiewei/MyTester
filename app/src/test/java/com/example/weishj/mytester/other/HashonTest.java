package com.example.weishj.mytester.other;

import com.example.weishj.mytester.JUnitBase;
import com.mob.tools.utils.Hashon;

import org.junit.Test;

import java.util.HashMap;

public class HashonTest extends JUnitBase {
	@Test
	public void testHash() throws Exception {
		HashMap<String, Object> map = new HashMap<>();
		map.put("id",  123);
		map.put("name", "test");

		Hashon hashon = new Hashon();
		String json = hashon.fromHashMap(map);

		TestEntity testEntity = hashon.fromJson(json, TestEntity.class);
		System.out.println(testEntity.toString());
	}

	private static class TestEntity {
		private int id;
		private String name;

		@Override
		public String toString() {
			return "{\"id\": " + id + ", \"name\": " + name + "}";
		}
	}
}
