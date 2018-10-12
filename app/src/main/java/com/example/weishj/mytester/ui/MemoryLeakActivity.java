package com.example.weishj.mytester.ui;

import android.os.Bundle;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;

public class MemoryLeakActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory_leak);
	}
}
