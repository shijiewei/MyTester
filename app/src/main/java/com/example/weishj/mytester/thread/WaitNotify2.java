package com.example.weishj.mytester.thread;

public class WaitNotify2 {
	public static byte[] lock = new byte[0];

	public static void main(String[] args) {
		Thread1 thread1 = new Thread1("t1");
		Thread2 thread2 = new Thread2("t2");

		thread1.start();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		thread2.start();
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
			synchronized (lock) {
				System.out.println("线程"+Thread.currentThread().getName()+" 开始");
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.notify();
				System.out.println("线程"+Thread.currentThread().getName()+"调用了object.notify()");
			}
			System.out.println("线程"+Thread.currentThread().getName()+"释放了锁");
		}
	}
}
