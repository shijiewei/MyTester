package com.example.weishj.mytester.concurrency.sync.synchronizedtest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition测试
 *
 * 生产者-消费者问题
 */
public class ConditionTest {
	private static final int REPOSITORY_SIZE = 3;
	private static final int PRODUCT_COUNT = 10;

	public static void main(String[] args)  {
		// 创建一个容量为REPOSITORY_SIZE的仓库
		final Repository repository = new Repository(REPOSITORY_SIZE);

		Thread producer = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < PRODUCT_COUNT; i++) {
					try {
						repository.put(Integer.valueOf(i));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}) ;

		Thread consumer = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < PRODUCT_COUNT; i++) {
					try {
						Object val = repository.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}) ;

		producer.start();
		consumer.start();
	}

	/**
	 * Repository 是一个定长集合，当集合为空时，take方法需要等待，直到有元素时才返回元素
	 * 当其中的元素数达到最大值时，put方法需要等待，直到元素被take之后才能继续put
	 */
	static class Repository {
		final Lock lock = new ReentrantLock();
		final Condition notFull = lock.newCondition();
		final Condition notEmpty = lock.newCondition();

		final Object[] items;
		int putIndex, takeIndex, count;

		public Repository(int size) {
			items = new Object[size];
		}

		public void put(Object x) throws InterruptedException {
			try {
				lock.lock();
				while (count == items.length) {
					System.out.println("Buffer full, please wait");
					// 开始等待库存不为满
					notFull.await();
				}

				// 生产一个产品
				items[putIndex] = x;
				// 增加当前库存量
				count++;
				System.out.println("Produce: " + x);
				if (++putIndex == items.length) {
					putIndex = 0;
				}
				// 通知消费者线程库存已经不为空了
				notEmpty.signal();
			} finally {
				lock.unlock();
			}
		}

		public Object take() throws InterruptedException {
			try {
				lock.lock();
				while (count == 0) {
					System.out.println("No element, please wait");
					// 开始等待库存不为空
					notEmpty.await();
				}
				// 消费一个产品
				Object x = items[takeIndex];
				// 减少当前库存量
				count--;
				System.out.println("Consume: " + x);
				if (++takeIndex == items.length) {
					takeIndex = 0;
				}
				// 通知生产者线程库存已经不为满了
				notFull.signal();
				return x;
			} finally {
				lock.unlock();
			}
		}
	}
}
