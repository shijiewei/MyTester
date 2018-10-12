package com.example.weishj.mytester.softreference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试类
 */
public class TestSoftReferenceCache {
	private static int MAX_COUNT = 10000;
	private static String KEY_PREFIX = "KEY_";
	private static SoftReferenceCache<String, byte[]> cache = new SoftReferenceCache<String, byte[]>();

	public static void main(String[] args) {
		ExecutorService es = Executors.newCachedThreadPool();
		es.submit(new Customer());
		es.submit(new Customer());
		es.submit(new Customer());
		es.submit(new Customer());
		es.submit(new Customer());
		es.shutdown();
	}

	static class Customer implements Runnable {

		@Override
		public void run() {
			while (true) {
				for (int i = 0; i < MAX_COUNT; i ++) {
					byte[] a = cache.get(KEY_PREFIX + i);
					if (a == null) {
						a = new byte[1024];
						cache.put(KEY_PREFIX + i, a);
						System.out.println(Thread.currentThread().getName() + " 向缓存池中添加对象[" + (KEY_PREFIX + i) + "]: " + a);
					} else {
						System.out.println(Thread.currentThread().getName() + " 从缓存池中获取对象[" + (KEY_PREFIX + i) + "]: " + a);
					}
				}
			}
		}
	}

}
