package com.example.weishj.mytester.thread;

public class YieldTester {

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			new MyThread().start();
		}
	}

	static class MyThread extends Thread {
		@Override
		public void run() {
			doWork();
		}

		private void doWork() {
			System.out.println(getName() + " start running");
			yield();
			System.out.println(getName() + " stop running");
		}
	}
}
