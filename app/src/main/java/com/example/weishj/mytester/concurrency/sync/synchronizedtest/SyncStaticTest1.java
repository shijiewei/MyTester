package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

/**
 * 同步安全测试
 *
 * 同步静态方法，实现线程安全
 */
public class SyncStaticTest1 implements Runnable {
	// 共享资源（临界资源）
	private static int race = 0;
	private static final int THREADS_COUNT = 10;

	public static synchronized void increase() {
		race++;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10000; i++) {
			// 这里加this只是为了显式地表明是通过对象来调用increase方法
			this.increase();
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			// 每次都创建新的SyncStaticTest1实例
			threads[i] = new Thread(new SyncStaticTest1());
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
