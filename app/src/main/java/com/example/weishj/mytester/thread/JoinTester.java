package com.example.weishj.mytester.thread;

public class JoinTester {
	public static void main(String[] args) {
		Thread previousThread = Thread.currentThread();
		for (int i = 1; i <= 10; i++) {
			Thread curThread = new JoinThread(previousThread);
			curThread.start();
			previousThread = curThread;
		}

		System.out.println(Thread.currentThread().getName() + " terminated.");
	}

	static class JoinThread extends Thread {
		private Thread thread;

		public JoinThread(Thread thread) {
			this.thread = thread;
		}

		@Override
		public void run() {
			try {
				// 试试打开下面两行代码看看运行结果
//				Thread.sleep(10);	// 这里完全可以不用sleep，只是为了更容易看到效果
//				System.out.println("I'm [" + Thread.currentThread().getName() + "], " +
//						"I need to wait [" + thread.getName()  +"] to terminate.");
				thread.join();
				System.out.println(Thread.currentThread().getName() + " terminated.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
