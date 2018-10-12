package com.example.weishj.mytester.thread;

/**
 * 启动三个线程A,B,C,各个线程只打印特定的字母，各打印10次，例如A线程只打印‘A’。要求在控制台依次显示“ABCABC…”
 */
public class WaitNotify {
	private final static Object lock = new Object();

	public static void main(String[] args) {
		Print print = new Print(10);
		new Thread(print, "A").start();
		new Thread(print, "B").start();
		new Thread(print, "C").start();
	}

	static class Print implements Runnable {
		private int max_print;
		private int count = 0;
		private String str = "A";

		public Print(int max_print) {
			this.max_print = max_print;
		}

		@Override
		public void run() {
			synchronized (lock) {
				String name = Thread.currentThread().getName();
				while (count < max_print) {
					if (str.equals(name)) {
						System.out.print(name);
						if (str.equals("A")) {
							str = "B";
						} else if (str.equals("B")) {
							str = "C";
						} else {
							count++;
							str = "A";
						}
						lock.notifyAll();
					} else {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
