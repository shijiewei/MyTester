package com.example.weishj.mytester.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;
import com.example.weishj.mytester.util.DeviceUtils;

public class DeviceInfoActivity extends BaseActivity {
	private TextView ipv6Tv;
	private TextView validateIpv6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_info);

		initView();
	}

	private void initView() {
		DeviceUtils dev = DeviceUtils.getInstance(this);
		ipv6Tv = findViewById(R.id.dev_ipv6_tv);
		String ipv6 = dev.getLocalIpV6();
		ipv6Tv.setText(ipv6);
		validateIpv6 = findViewById(R.id.dev_validate_ipv6_tv);
		String isValidateIpv6 = dev.validateV6();
		validateIpv6.setText(isValidateIpv6);
	}
}
