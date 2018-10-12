package com.example.weishj.mytester.collect;

import android.content.Context;
import android.os.Message;

import java.io.File;

/**
 * Created by weishj on 2018/3/29.
 */

public class ArtClt extends BaseClt {
	private static final String TAG = ArtClt.class.getSimpleName();
	private static final int MSG_ART_START = 1001;

	protected File getLockFile(Context context) {
		return Locks.getFileLock(context, Locks.APP_RUNNING_TIME_COLLECTOR_LOCK);
	}

	@Override
	protected void initSendMsg() {
		// 在本线程中对自己的handler发送消息，将阻塞handleMessage的执行，只有发送任务全部执行完毕后，才会开始handleMessage
//		for (int i = 1; i <= 2; i ++) {
//			Message message = new Message();
//			message.what = i;
//			message.obj = "ART " + i;
//			MyLog.d(TAG, "发送消息 " + message.obj);
//
//			if (i == 2) {
//				removeMessages(i - 1);
//			}
//			sendMessage(message);	// 虽然1、2都发送出去了，但最终只有2被消费，相当于阻止消息消费
//		}
		// 新开线程发送消息，将不会阻塞handler所在的线程接收消息
		new Thread() {
			@Override
			public void run() {
				for (int i = 1; i <= 2; i ++) {
					Message message = new Message();
					message.what = i;
					message.obj = "ART " + i;
					MyLog.d(TAG, "发送消息 " + message.obj);

					if (i == 2) {
						removeMessages(i - 1);
					}
//					sendMessage(message);	// 1、2都能收到
			sendMessageDelayed(message, 1000);	// 只能收到2，因为1发送出去前已经被remove了，相当于阻止消息发送
				}
			}
		}.start();
	}

	@Override
	protected void handleMsg(Message msg) {
		switch (msg.what) {
			case 1: {
				MyLog.d(TAG, "收到消息 " + msg.obj);
				MyLog.d(TAG, "收到消息1后休眠 3s");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} break;
			case 2: {
				MyLog.d(TAG, "收到消息 " + msg.obj);
				MyLog.d(TAG, "收到消息2后休眠 3s");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} break;
		}
	}
}
