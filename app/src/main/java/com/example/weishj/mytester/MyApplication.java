package com.example.weishj.mytester;

import android.app.Application;

import com.example.weishj.mytester.collect.GlobalInit;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		GlobalInit.init(this);
	}
}
