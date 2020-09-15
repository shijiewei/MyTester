package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

/**
 * 同步安全测试
 *
 * 脱离了"同一个对象"的前提，synchronized实例方法将不再具有同步安全性
 */
public class SyncTest3 implements Runnable {
	// 共享资源（临界资源）
	private static int race = 0;
	private static final int THREADS_COUNT = 10;

	// synchronized实例方法，安全访问临界资源
	public synchronized void increase() {
		race++;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10000; i++) {
			increase();
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
//		SyncTest3 runnable = new SyncTest3();
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			// 不同的对象锁，将导致临界资源不再安全
			threads[i] = new Thread(new SyncTest3());
			threads[i].start();
		}

		// 等待所有累加线程都结束
		while (Thread.activeCount() > 1) {
			Thread.yield();
		}
		// 期待的结果应该是（THREADS_COUNT * 10000）= 100000
		System.out.println("race = " + race + ", time: " + (System.currentTimeMillis() - start));
	}
}
