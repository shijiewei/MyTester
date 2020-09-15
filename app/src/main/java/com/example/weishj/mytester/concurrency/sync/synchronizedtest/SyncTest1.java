package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

/**
 * 同步安全测试
 *
 * 在无任何同步措施时，并发会导致错误的结果
 */
public class SyncTest1 implements Runnable {
	// 共享资源（临界资源）
	private static int race = 0;
	private static final int THREADS_COUNT = 10;

	public void increase() {
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
		SyncTest1 runnable = new SyncTest1();
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			threads[i] = new Thread(runnable);
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
