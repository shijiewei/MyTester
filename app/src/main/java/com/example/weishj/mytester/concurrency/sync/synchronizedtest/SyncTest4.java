package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

/**
 * 同步安全测试
 *
 * 同一个对象的不同synchronized实例方法之间，也是互斥的
 */
public class SyncTest4 {
	private static final int THREADS_COUNT = 2;

	public synchronized void a() {
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
		final SyncTest4 instance = new SyncTest4();
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			final int finalI = i;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					if (finalI % 2 == 0) {
						// 若通过不同对象调用方法ab，则ab之间不存在互斥关系
//						new SyncTest4().a();
						// 在同一个对象上调用方法ab，则ab之间是互斥的
						instance.a();
					} else {
						// 若通过不同对象调用方法ab，则ab之间不存在互斥关系
//						new SyncTest4().b();
						// 在同一个对象上调用方法ab，则ab之间是互斥的
						instance.b();
					}
				}
			});
			threads[i].start();
		}
	}
}
