package com.example.weishj.mytester.ui;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;
import com.example.weishj.mytester.fileobserver.FileWatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

public class FileMonitorActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = FileMonitorActivity.class.getSimpleName();
	private static final String FILE_NAME = "test.txt";
	private FileWatcher fileWatcher;
	private String path;
	private String fullPath;
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_monitor);
		// File monitor test
		prepareFileMonitorTest();
		initView();
		index = 0;
		monitorExternalDir();
		createFileUnderFileDir();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		fileWatcher.stopWatching();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_file_monitor_create) {
			createFile();
		} else if (id == R.id.btn_file_monitor_modify) {
			modifyFile();
		}
	}

	private void initView() {
		findViewById(R.id.btn_file_monitor_create).setOnClickListener(this);
		findViewById(R.id.btn_file_monitor_modify).setOnClickListener(this);
	}

	private void prepareFileMonitorTest() {
		// Y	/data/data/com.example.weishj.mytester/cache/test/test.txt
//		path = getCacheDir() + "/test/";
		// Y 	/data/data/com.example.weishj.mytester/files/test/test.txt
//		path = getFilesDir() + "/test/";
		// Y	Android/data/com.example.weishj.mytester/cache/test/
//		path = getExternalCacheDir() + "/test/";
		// Y	/storage/emulated/0/test/test.txt
//		path = Environment.getExternalStorageDirectory() + "/test/";
		// Y	/storage/emulated/0/Android/data/com.example.weishj.mytester/files/DCIM/test/test.txt
		path = getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/test/";
		fileWatcher = new FileWatcher(path);
		fileWatcher.startWatching();

		Log.d(FileWatcher.TAG, "path: " + path);
		final File file = new File(path);
		if(!file.exists()) {
			file.mkdir();
		}
	}

	private void createFile() {
		index = 0;
		fullPath = path + FILE_NAME;
		File newFile = new File(fullPath);
		if (!newFile.exists()) {
			try {
				Log.d(FileWatcher.TAG, "create file: " + fullPath);
				Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
				newFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.d(FileWatcher.TAG, "delete file: " + fullPath);
			Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
			newFile.delete();
		}
	}

	private void modifyFile() {
		try{
			File file = new File(fullPath);
			FileOutputStream fos;
			if(!file.exists()){
				file.createNewFile();//如果文件不存在，就创建该文件
				fos = new FileOutputStream(file);//首次写入获取
			}else{
				//如果文件已存在，那么就在文件末尾追加写入
				fos = new FileOutputStream(file,true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
			}
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件
			index ++;
			osw.write(index + "\r\n");
			//写入完成关闭流
			osw.close();
			long update = file.lastModified();
			Log.d(FileWatcher.TAG, "update: " + update);
			Toast.makeText(this, "Appended: " + index, Toast.LENGTH_SHORT).show();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createFileUnderFileDir() {
		try {
			FileOutputStream os = openFileOutput("test.txt",MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
			os.write("哈哈哈哈".getBytes());
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void monitorExternalDir() {
		String externalPath = "/storage/emulated/0/Android/data/";
		File exPath = new File(externalPath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss sss");
		if (exPath != null && exPath.isDirectory()) {
			File[] files = exPath.listFiles();
			Log.d(FileWatcher.TAG, "size: " + files.length);
			for (File file : files) {
				Log.d(FileWatcher.TAG, file.getAbsolutePath() + ", lastUpdate: " + sdf.format(file.lastModified()));
			}
		} else {
			Log.d(FileWatcher.TAG, "Can not read path: " + externalPath);
		}
	}
}
