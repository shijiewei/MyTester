package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

/**
 * 同步安全测试
 *
 * 一个简单的售票程序，多线程同时售票时，会出现线程安全问题
 */
public class ReentrantLockTest1 {
	private static final int THREADS_COUNT = 3;	// 线程数
	private static final int TICKETS_PER_THREAD = 5;	// 每个线程分配到的票数
	// 共享资源（临界资源）
	private int ticket = THREADS_COUNT * TICKETS_PER_THREAD;	// 总票数

	public void buyTicket() {
		try {
			if (ticket > 0) {
				System.out.println("Thread: " + Thread.currentThread().getName() + ", bought ticket-" + ticket--);
				// 为了更容易出现安全问题，这里加一个短暂睡眠
				Thread.sleep(2);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void readTicket() {
		System.out.println("Thread: " + Thread.currentThread().getName() + ", tickets left: " + ticket);
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		final ReentrantLockTest1 instance = new ReentrantLockTest1();
		// 启动 THREADS_COUNT 个线程
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					// 每个线程可以卖 TICKETS_PER_THREAD 张票
					for (int j = 0; j < TICKETS_PER_THREAD; j++) {
						instance.buyTicket();
					}
				}
			});
			threads[i].start();
		}

		// 等待所有累加线程都结束
		while (Thread.activeCount() > 1) {
			Thread.yield();
		}

		// 读取剩余票数
		instance.readTicket();
		// 耗时
		System.out.println("time: " + (System.currentTimeMillis() - start));
	}
}
