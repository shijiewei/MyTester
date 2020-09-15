package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 同步安全测试
 *
 * 演示ReentrantReadWriteLock实现同步，它的特点是"读读并发"、"写写互斥"、"读写互斥"
 */
public class ReentrantReadWriteLockTest1 {
	private static final int THREADS_COUNT = 3;	// 线程数
	private static final int TICKETS_PER_THREAD = 4;	// 每个线程分配到的票数
	// 共享资源（临界资源）
	private int ticket = THREADS_COUNT * TICKETS_PER_THREAD;	// 总票数
	private static final ReadWriteLock lock;

	static {
		// 为了通过一个示例同时演示"读并发"、"写互斥"、"读写互斥"的效果，创建一个公平锁
		lock = new ReentrantReadWriteLock(true);	// 此处也说明读锁与写锁之间同样遵守公平性原则
	}

	public void buyTicket() {
		try {
			lock.writeLock().lock();
			if (ticket > 0) {
				System.out.println("Thread: " + Thread.currentThread().getName() + ", bought ticket-" + ticket--);
				Thread.sleep(2);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.out.println("Thread: " + Thread.currentThread().getName() + ", unlocked write");
			lock.writeLock().unlock();
		}
	}

	public void readTicket() {
		try {
			lock.readLock().lock();
			System.out.println("Thread: " + Thread.currentThread().getName() + ", tickets left: " + ticket);
			Thread.sleep(5);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.out.println("Thread: " + Thread.currentThread().getName() + ", unlocked read");
			lock.readLock().unlock();
		}
	}

	public static void main(String[] args) {
		final ReentrantReadWriteLockTest1 instance = new ReentrantReadWriteLockTest1();
		// 启动 THREADS_COUNT 个线程
		Thread[] writeThreads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			writeThreads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					// 每个线程可以卖 TICKETS_PER_THREAD 张票
					for (int j = 0; j < TICKETS_PER_THREAD; j++) {
						instance.buyTicket();
					}
				}
			});
			writeThreads[i].start();
		}

		// 读取此时的剩余票数
		Thread[] readThreads = new Thread[2];
		for (int i = 0; i < 2; i++) {
			readThreads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					// 每个线程可以读 2 次剩余票数
					for (int j = 0; j < 2; j++) {
						instance.readTicket();
					}
				}
			});
			readThreads[i].start();
		}
	}
}
