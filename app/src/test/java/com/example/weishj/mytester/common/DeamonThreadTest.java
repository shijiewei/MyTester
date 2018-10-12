package com.example.weishj.mytester.common;

public class DeamonThreadTest {
	public static void main(String[] args) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000l);
				} catch (InterruptedException e) {
					//
				} finally {
					System.out.println("DeamonThread finally run.");
				}
			}
		}, "DeamonRunner");
//		thread.setDaemon(true);
		thread.start();
	}

	static class DeamonRunner implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(2000l);
			} catch (InterruptedException e) {
				//
			} finally {
				System.out.println("DeamonThread finally run.");
			}
		}

	}
}