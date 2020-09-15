package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

/**
 * 同步安全测试
 *
 * 同步静态方法和同步实例方法之间，不存在互斥性
 */
public class SyncStaticTest2 {
	private static final int THREADS_COUNT = 2;

	public synchronized static void a() {
		int i = 5;
		while (i-- > 0) {
			System.out.println("Thread: " + Thread.currentThread().getName() + ", method: a, running...");
		}
	}

	public synchronized void b() {
		int i = 5;
		while (i-- > 0) {
			System.out.println("Thread: " + Thread.currentThread().getName() + ", method: b, running...");
		}
	}

	public static void main(String[] args) {
		final SyncStaticTest2 instance = new SyncStaticTest2();
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			final int finalI = i;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					if (finalI % 2 == 0) {
						// 静态方法即可以通过实例调用，也可以通过类调用
						instance.a();
					} else {
						// 实例方法则只能通过实例调用
						instance.b();
					}
				}
			});
			threads[i].start();
		}
	}
}
