package com.example.weishj.mytester.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by weishj on 2017/6/21.
 */

public abstract class MyHandlerThread implements Handler.Callback {
	protected static final int START = -1;
	protected static final int call_onMessage = 0;
	protected final Handler handler;

	public MyHandlerThread() {
		outputLog("Init MyHandlerThread.");
		android.os.HandlerThread t2 = new android.os.HandlerThread("MyHandlerThread");
		t2.start();
		outputLog("Create t2.");
		this.handler = new Handler(t2.getLooper(), this); // handler持有的是t2的Looper和MessageQueue
	}

	public void startThread() {
		outputLog("start thread.");
		Message msg = new Message();
		msg.what = START;
		this.handler.sendMessage(msg);
	}

	public final boolean handleMessage(Message msg) {
//		outputLog("MyHandlerThread handleMessage. msg.what: " + msg.what);
		// 处理this.handler发过来的消息
		switch(msg.what) {
			case START:
				onStart(msg);
				break;
			case call_onMessage:
				onMessage(msg);
				break;
		}
		return false;
	}

	protected void outputLog(String msg) {
		Log.d("HandlerTest", "[ThreadName: " + Thread.currentThread().getName() +
				", threadId: " + Thread.currentThread().getId() + "] " + msg);
	}

	protected abstract void onStart(Message msg);
	protected abstract void onMessage(Message var1);
}
