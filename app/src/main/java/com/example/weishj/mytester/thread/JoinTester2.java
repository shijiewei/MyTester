package com.example.weishj.mytester.thread;

public class JoinTester2 {
	public static void main(String[] args) {
		int size = 5;
		MyThread[] arr = new MyThread[size];
		for (int i = 0; i < size; i++) {
			arr[i] = new MyThread(i);
			arr[i].start();
		}
		String output = "";
		try {
			arr[0].join();
			output += arr[0].get();
			arr[1].join();
			output += arr[1].get();
			arr[2].join();
			output += arr[2].get();
			arr[3].join();
			output += arr[3].get();
			arr[4].join();
			output += arr[4].get();
			System.out.println("Result: " + output);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static class MyThread extends Thread {
		private int i;

		public MyThread(int i) {
			setName("T-" + i);
			this.i = i;
		}

		@Override
		public void run() {
			this.i++;
			System.out.println("I'm [" + getName() + "], i is now " + this.i);
		}

		public int get() {
			return this.i;
		}
	}
}
