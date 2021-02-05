package com.example.weishj.mytester.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;
import com.example.weishj.mytester.util.Utils;
import com.mob.tools.utils.UIHandler;

public class OtherTestActivity extends BaseActivity {
	private static final String TAG = "FileShareActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_test);

		initView();
	}

	private void initView() {
		String title = getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.title_other_test);
		((TextView)findViewById(R.id.id_other_test_title_tv)).setText(title);

		findViewById(R.id.page_other_test_context__btn).setOnClickListener(this);
	}

	@Override
	protected void onViewClicked(View v) {
		int id = v.getId();
		if (id == R.id.page_other_test_context__btn) {
			obtainContext();
		}
	}

	private void obtainContext() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Context ctt = Utils.getContext();
				Log.e(TAG, "Sub context: " + ctt);
				UIHandler.sendEmptyMessage(0, new Handler.Callback() {
					@Override
					public boolean handleMessage(Message message) {
						Toast.makeText(OtherTestActivity.this, "Sub: " + ctt, Toast.LENGTH_SHORT).show();
						return false;
					}
				});
			}
		}).start();

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Context ctt = Utils.getContext();
				Log.e(TAG, "Main context: " + ctt);
				Toast.makeText(OtherTestActivity.this, "Main: " + ctt, Toast.LENGTH_SHORT).show();
			}
		}, 3000);
	}
}
