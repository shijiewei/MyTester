package com.example.weishj.mytester.collect;

import android.content.Context;
import android.os.Message;

import java.io.File;

/**
 * Created by weishj on 2018/3/29.
 */

public class PkgClt extends BaseClt {
	private static final String TAG = PkgClt.class.getSimpleName();
	private static final int PKG = 2001;

	protected File getLockFile(Context context) {
		return Locks.getFileLock(context, Locks.PACKAGE_COLLECTOR_LOCK);
	}

	@Override
	protected void initSendMsg() {
		for (int i = 1; i <= 2; i ++) {
			Message message = new Message();
			message.what = PKG;
			message.obj = "PKG " + i;
			MyLog.d(TAG, "发送消息 " + message.obj);
//			sendMessageDelayed(message, 1000);
			sendMessage(message);
		}
	}

	@Override
	protected void handleMsg(Message msg) {
		switch (msg.what) {
			case PKG: {
				MyLog.d(TAG, "收到消息 " + msg.obj);
			} break;
		}
	}
}
