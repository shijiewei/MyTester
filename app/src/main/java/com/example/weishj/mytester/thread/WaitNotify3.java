package com.example.weishj.mytester.thread;

public class WaitNotify3 {
	public static byte[] lock = new byte[0];

	public static void main(String[] args) {
		Thread1 thread1 = new Thread1("t1");
		Thread2 thread2 = new Thread2("t2");
		// 正确，先执行1，后执行2，无死锁
		testCase(thread1, thread2);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		System.out.println("开始case2");
//		Thread1 thread3 = new Thread1("t3");
//		Thread2 thread4 = new Thread2("t4");
//		// 错误，先执行2，后执行1，线程1会永远wait
//		testCase(thread4, thread3);
	}

	private static void testCase(Thread firstRun, Thread secondRun) {
		firstRun.start();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		secondRun.start();
	}

	static class Thread1 extends Thread{
		public Thread1(String name) {
			super(name);
		}

		@Override
		public void run() {
			synchronized (lock) {
				System.out.println("线程"+Thread.currentThread().getName()+" 开始");
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
				System.out.println("线程"+Thread.currentThread().getName()+" 等待结束");
			}
		}
	}

	static class Thread2 extends Thread{
		public Thread2(String name) {
			super(name);
		}

		@Override
		public void run() {
			try {
				synchronized (lock) {
					System.out.println("线程"+Thread.currentThread().getName()+" 开始");
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					throw new RuntimeException("test exception");
				}
			} finally {
				synchronized (lock) {
					lock.notifyAll();
					System.out.println("线程"+Thread.currentThread().getName()+"调用了object.notify()");
				}
			}
		}
	}
}
