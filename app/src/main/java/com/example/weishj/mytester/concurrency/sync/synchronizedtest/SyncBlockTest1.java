package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

/**
 * 同步安全测试
 *
 * 同步代码块，实现线程安全
 */
public class SyncBlockTest1 implements Runnable {
	// 共享资源（临界资源）
	private static int race = 0;
	private static final int THREADS_COUNT = 10;
	// 使用一个长度为0的byte数组作为对象锁
	private byte[] lock = new byte[0];

	public void increase() {
		race++;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10000; i++) {
			// 要注意这里锁定的对象是谁
			synchronized (SyncBlockTest1.class) {
				increase();
			}
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		SyncBlockTest1 runnable = new SyncBlockTest1();
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			// 每次都创建新的SyncStaticTest1实例
			threads[i] = new Thread(new SyncBlockTest1());
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
