package com.example.weishj.mytester.concurrency;

/**
 * DCL(双锁检查）单例
 *
 * 线程安全的
 */
public class ConcurrentSingleton {
	// 必须使用volatile修饰
	private volatile static ConcurrentSingleton instance;

	private ConcurrentSingleton() {}

	public static ConcurrentSingleton getInstance() {
		// 如果有任意一个线程执行到这里时发现instance已经指向了一个内存空间（此时可能由于指令重排序，导致Singleton对象还未初始化），
		// 就会错误地拿到一个instance，它指向一个内存空间，但是该空间内还没有初始化完成的Singleton对象
		if (instance == null) {
			// 如果有其他线程正在这里等待锁，那么指令重排序也不会造成问题，因为在锁释放前这些线程只能等待
			synchronized (ConcurrentSingleton.class) {
				if (instance == null) {
					// 关键就是这句赋值操作，它并非原子操作，可以拆分为：分配内存空间、初始化对象、将instance指向刚分配的内存空间
					instance = new ConcurrentSingleton();
				}
			}
		}
		return instance;
	}
}
