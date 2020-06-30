package com.example.weishj.mytester.thread;

public class DeamonThreadTest {
	public static void main(String[] args) {
		// 验证一：Deamon线程的finally块，不保证执行，
		// 若执行到finally之前所有user线程都结束，则deamon线程也会随着jvm退出而结束，不会执行finally
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					System.out.println("DeamonThread finally run.");
				}
			}
		});
		thread.setDaemon(true);
		thread.start();

		// 验证二：main线程结束，deamon线程即终止，不会等待执行完毕
		Runnable r = new Runnable() {
			public void run() {
				for (int time = 10; time > 0; --time) {
					System.out.println("Time #" + time);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		Thread t = new Thread(r);
		t.setDaemon(false);  // try to set this to "false" and see what happens
		t.start();

		System.out.println("Main thread waiting...");
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main thread exited.");
	}
}