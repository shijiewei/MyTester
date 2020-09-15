package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步安全测试
 *
 * 演示ReentrantLock实现同步，以及公平锁与非公平锁
 */
public class ReentrantLockTest2 {
	private static final int THREADS_COUNT = 3;	// 线程数
	private static final int TICKETS_PER_THREAD = 5;	// 每个线程分配到的票数
	// 共享资源（临界资源）
	private int ticket = THREADS_COUNT * TICKETS_PER_THREAD;	// 总票数
	private static final ReentrantLock lock;

	static {
		// 创建一个公平锁/非公平锁
		lock = new ReentrantLock(true);	// 修改参数，看看公平锁与非公平锁的差别
	}

	public void buyTicket() {
		try {
			lock.lock();
			if (ticket > 0) {
				System.out.println("Thread: " + Thread.currentThread().getName() + ", bought ticket-" + ticket--);
				// 为了演示出公平锁与非公平锁的效果，这里加一个短暂睡眠，让其他线程获得一个等待时间
				Thread.sleep(2);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			// unlock应该放在finally中，防止发生异常时来不及解锁
			lock.unlock();
		}
	}

	public void readTicket() {
		System.out.println("Thread: " + Thread.currentThread().getName() + ", tickets left: " + ticket);
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		final ReentrantLockTest2 instance = new ReentrantLockTest2();
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
