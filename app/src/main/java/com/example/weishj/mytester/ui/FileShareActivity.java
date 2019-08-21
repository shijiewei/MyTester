package com.example.weishj.mytester.ui;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class FileShareActivity extends BaseActivity {
	private static final String TAG = "FileShareActivity";
	private static final String TEST_PATH = Environment.getExternalStorageDirectory() + "/AMyTest/";
	private static final String TEST_NAME = ".test";
	private Button createFileBtn;
	private Button readFileBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_share);
		initView();
	}

	@Override
	protected void onViewClicked(View v) {
		int id = v.getId();
		if (id == R.id.page_test_create_file_btn) {
			createFile(TEST_PATH, TEST_NAME);
		} else if (id == R.id.page_test_read_file_btn) {
			readFile(TEST_PATH, TEST_NAME);
		}
	}

	private void initView() {
		createFileBtn = findViewById(R.id.page_test_create_file_btn);
		createFileBtn.setOnClickListener(this);
		readFileBtn = findViewById(R.id.page_test_read_file_btn);
		readFileBtn.setOnClickListener(this);
	}

	private void createFile(String path, String name) {
		File file = new File(path, name);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			boolean rst = file.createNewFile();
			if (rst) {
				String str = "This is content";
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
				osw.append(str);
				osw.flush();
				osw.close();
				Log.d(TAG, "File created. " + file.getAbsolutePath());
			} else {
				Log.d(TAG, "File already exists. " + file.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readFile(String path, String name) {
		File file = new File(path, name);
		if (file != null && file.exists()) {
			StringBuilder sb = new StringBuilder();
			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String line = br.readLine();
				while (line != null) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(line);
					line = br.readLine();
				}
				br.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			Log.d(TAG, "File read successfully: " + sb.toString());
		} else {
			Log.d(TAG, "File read failed. " + file);
		}
	}
}
