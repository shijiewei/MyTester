package com.example.weishj.mytester.handler;

import android.os.Message;
import android.os.SystemClock;

/**
 * Created by weishj on 2017/6/21.
 */

public class Sub extends MyHandlerThread {
	private static final int DELAY = 500;

	public void printHello(String type, boolean async) {
		for (int i = 0; i < 5; i++) {
			Message msg = new Message();
			msg.what = call_onMessage;
			msg.arg1 = i + 1;
			msg.obj = type;
			if (async) {
				super.handler.sendMessage(msg);
				SystemClock.sleep(DELAY);
			} else {
				onMessage(msg);
				SystemClock.sleep(DELAY);
			}
		}
	}

	@Override
	protected void onStart(Message msg) {
		/* true 或 false能否实现同步异步的控制
		 * true：输出为：① - ②ONE - ②TWO：
		 * 		此时的处理过程如下：
		 * 			1.Looper从消息队列中拿到START的消息，开始调用onStart处理，
		 * 			2.首先将5次②ONE发送到消息队列中，但由于此时的onStart还没执行完，所以这些②ONE并不能被执行，只是处于排队中，
		 * 			3.开始执行①
		 * 			4.将5次②TWO发送到消息队列中，此时由第一个START消息触发的onStart处理完毕，Looper可以开始从消息队列中取下一条消息
		 * 			5.此时消息队列中就是之前被插入的10条消息，5条②ONE 和 5条②TWO被一次消费
		 * 		整个过程中，消息队列中共11条消息，依次为：1个START - 5个call_onMessage（②ONE） - 5个call_onMessage（②TWO）
		 * false：输出为：②ONE - ① - ②TWO
		 * 		此时的处理过程如下：
		 * 			1.Looper从消息队列中拿到START的消息，开始调用onStart处理，
		 * 			2.打印5次②ONE
		 * 			3.打印5次①
		 * 			4.打印5次②TWO
		 * 		整个过程中，消息队列中只有1条消息：1个START
		 */
		outputLog("Before call printHello().");
		printHello("ONE", true);
		for (int i = 0; i < 5; i++) {
			outputLog("After " + (i + 1));	// 打印语句①
			SystemClock.sleep(DELAY);
		}
		printHello("TWO", true);
	}

	@Override
	protected void onMessage(Message msg) {
		outputLog("Print hello " + msg.obj + ": " + msg.arg1);	// 打印语句②
	}
}
