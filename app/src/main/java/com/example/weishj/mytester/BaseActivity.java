package com.example.weishj.mytester;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import com.example.weishj.mytester.util.MultiLanguageUtil;

/**
 * Created by weishj on 2018/4/25.
 */

public class BaseActivity extends Activity {
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		// base不是ApplicationContext
		MultiLanguageUtil.getInstance().setConfiguration(getApplicationContext());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		MultiLanguageUtil.getInstance().setConfiguration(getApplicationContext());
	}
}
