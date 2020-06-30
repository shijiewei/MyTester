package com.example.weishj.mytester.thread;

public class SleepWaitTester {
	private static byte[] lock = new byte[0];

	public static void main(String[] args) {
		for (int i = 0; i < 2; i++) {
			new MyThread().start();
		}
	}

	static class MyThread extends Thread {
		@Override
		public void run() {
			doWork();
		}

		private void doWork() {
			synchronized (lock) {
				try {
					System.out.println(getName() + " start running");
					lock.wait(1000);	// 换成lock.wait(1000)试试看
					System.out.println(getName() + " stop running");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
