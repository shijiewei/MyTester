package com.example.weishj.mytester.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;
import com.example.weishj.mytester.fileobserver.FileWatcher;
import com.example.weishj.mytester.util.DateUtils;
import com.mob.tools.MobLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileMonitorActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = FileMonitorActivity.class.getSimpleName();
	private static final String FILE_NAME = "test.txt";
	private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
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
//		createFileUnderFileDir();
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
		} else if (id == R.id.btn_file_monitor_read_time) {
			readTestFileTime();
		} else if (id == R.id.btn_file_monitor_scan) {
//			monitorExternalDir();
			scanExternalDir();
		}
	}

	private void initView() {
		findViewById(R.id.btn_file_monitor_create).setOnClickListener(this);
		findViewById(R.id.btn_file_monitor_modify).setOnClickListener(this);
		findViewById(R.id.btn_file_monitor_scan).setOnClickListener(this);
		findViewById(R.id.btn_file_monitor_read_time).setOnClickListener(this);
	}

	private void prepareFileMonitorTest() {
		// Y	/data/data/com.example.weishj.mytester/cache/test/test.txt
//		path = getCacheDir() + "/test/";
		// Y 	/data/data/com.example.weishj.mytester/files/test/test.txt
//		path = getFilesDir() + "/test/";
		// Y	Android/data/com.example.weishj.mytester/cache/test/
		path = getExternalCacheDir() + "/test/";
		// Y	/storage/emulated/0/test/test.txt
//		path = Environment.getExternalStorageDirectory() + "/test/";
		// Y	/storage/emulated/0/Android/data/com.example.weishj.mytester/files/DCIM/test/test.txt
//		path = getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/test/";
		fullPath = path + FILE_NAME;
		fileWatcher = new FileWatcher(path);
		fileWatcher.startWatching();

		Log.d(FileWatcher.TAG, "path: " + path);
		final File file = new File(path);
		if(!file.exists()) {
			file.mkdirs();
		}
	}

	private void createFile() {
		index = 0;
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

			Log.d(FileWatcher.TAG, "updateBefore: " + DateUtils.getDefaultTime(file.lastModified()) + ", file: " + fullPath);
			file.setLastModified(System.currentTimeMillis() + 1000000);
			Log.d(FileWatcher.TAG, "updateAfter0: " + DateUtils.getDefaultTime(file.lastModified()) + ", file: " + fullPath);

			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件
			index ++;
			osw.write(index + "\r\n");
			//写入完成关闭流
			osw.close();

			Log.d(FileWatcher.TAG, "updateAfter1: " + DateUtils.getDefaultTime(file.lastModified()) + ", file: " + fullPath);
			file.setLastModified(System.currentTimeMillis() + 1000000);
			Log.d(FileWatcher.TAG, "updateAfter2: " + DateUtils.getDefaultTime(file.lastModified()) + ", file: " + fullPath);

			String parentPath = "/storage/emulated/0/Android/data/com.example.weishj.mytester/";
			File file1 = new File(parentPath);
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					BasicFileAttributes attributes = Files.readAttributes(file1.toPath(), BasicFileAttributes.class);
					Log.d(FileWatcher.TAG, "Files.getLastModifiedTime: " + Files.getLastModifiedTime(file1.toPath()) + ", file: " + parentPath);
					Log.d(FileWatcher.TAG, "attributes.createTime: " + attributes.creationTime().toString() + ", file: " + parentPath);
					Log.d(FileWatcher.TAG, "attributes.modifyTime: " + attributes.lastModifiedTime().toString() + ", file: " + parentPath);
					Log.d(FileWatcher.TAG, "attributes.accessTime: " + attributes.lastAccessTime().toString() + ", file: " + parentPath);
					Log.d(FileWatcher.TAG, "attributes.size: " + attributes.size() + ", file: " + parentPath);
				}
			} catch (Throwable t) {
				Log.e(FileWatcher.TAG, t.getMessage(), t);
			}
			if (file1 != null && file1.exists()) {
				Log.d(FileWatcher.TAG, "update: " + file1.lastModified() + ", file: " + parentPath);
			}
			Toast.makeText(this, "Appended: " + index, Toast.LENGTH_SHORT).show();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE 从7.0开始已经被废弃，如果在7.0以上系统调用将直接crash
	 */
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
		// "/storage/emulated/0/Android/data/"
		String externalPath = Environment.getExternalStorageDirectory() + "/Android/data/";
		File exPath = new File(externalPath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss sss");
		if (exPath != null && exPath.isDirectory()) {
			File[] files = exPath.listFiles();
			Log.d(FileWatcher.TAG, "size: " + files.length);
			Toast.makeText(this, "target: " + externalPath + "\nsize: " + files.length, Toast.LENGTH_SHORT).show();
			for (File file : files) {
				Log.d(FileWatcher.TAG, file.getAbsolutePath() + ", lastUpdate: " + sdf.format(file.lastModified()));
			}
		} else {
			Log.d(FileWatcher.TAG, "Can not read path: " + externalPath);
		}
	}

	private void scanExternalDir() {
		// "/storage/emulated/0/Android/data/"
		String externalPath = Environment.getExternalStorageDirectory() + "/Android/data/";
		File exPath = new File(externalPath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss sss");
		if (exPath != null && exPath.isDirectory()) {
			File[] files = exPath.listFiles();
			Toast.makeText(this, "target: " + externalPath + "\nsize: " + files.length, Toast.LENGTH_SHORT).show();
			long start = System.currentTimeMillis();
			int size = 0;
			for (File file : files) {
				if (isPkg(file)) {
					size ++;
					Log.d(FileWatcher.TAG, file.getName() + ", lastUpdate: " + sdf.format(getUpdateTime(file.getAbsolutePath())));
				}
			}
			long end = System.currentTimeMillis();
			long duration = (end - start);
			Log.d(FileWatcher.TAG, "total: " + files.length + ", pkg: " + size + ", duration: " + duration + " ms");
		} else {
			Log.d(FileWatcher.TAG, "Can not read path: " + externalPath);
		}
	}

	private boolean isPkg(File file) {
		boolean isPkg = false;
		if (file != null && file.isDirectory()) {
			// Java/Android合法包名，可以包含大写字母、小写字母、数字和下划线，用点(英文句号)分隔称为段，且至少包含2个段，隔开的每一段都必须以字母开头
			Pattern pattern = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)+([.][a-zA-Z_][a-zA-Z0-9_]*)+$");
			Matcher matcher = pattern.matcher(file.getName());
			isPkg = matcher.matches();
		}
		return isPkg;
	}

	/**
	 * 获取指定文件夹的最近更新时间
	 *
	 * 由于文件夹下的子目录内容更新时，父目录本身的更新时间不会变，因此需要递归遍历文件夹下所有子目录和子文件，取最近的更新时间作为该文件夹的更新时间，
	 * 递归调用容易发生StackOverflowError，因此使用非递归算法实现
	 *
	 * @param path 文件夹路径
	 * @return
	 */
	private long getUpdateTime(String path) {
		int fileNum = 0;
		int folderNum = 0;
		long update = 0L;
		File file = new File(path);
		if (file != null && file.exists()) {
			update = file.lastModified();
			LinkedList<File> list = new LinkedList<>();
			File[] subFiles = file.listFiles();
			if (subFiles != null) {
				for (File subFile : subFiles) {
					if (subFile != null) {
						update = subFile.lastModified();
						if (subFile.isDirectory()) {
							list.add(subFile);
							folderNum++;
						} else {
							fileNum++;
						}
					}
				}
				File temp_file;
				while (!list.isEmpty()) {
					temp_file = list.removeFirst();
					subFiles = temp_file.listFiles();
					if (subFiles != null) {
						for (File subFile : subFiles) {
							if (subFile != null) {
								long newTime = subFile.lastModified();
								if (newTime > update) {
									update = newTime;
								}
								if (subFile.isDirectory()) {
									list.add(subFile);
									folderNum++;
								} else {
									fileNum++;
								}
							}
						}
					}
				}
			}
		} else {
			Log.d(FileWatcher.TAG, "Directory not exists. path: " + path);
		}
//		Log.d(FileWatcher.TAG, "folder: " + folderNum + ", file: " + fileNum + ", path: " + path);
		return update;
	}

	private boolean isSystemApp(String pkgName) {
		boolean isSystemApp = false;
		PackageInfo pi = null;
		try {
			PackageManager pm = getApplicationContext().getPackageManager();
			pi = pm.getPackageInfo(pkgName, 0);
		} catch (Throwable t) {
			Log.w(TAG, t.getMessage(), t);
		}
		// 是系统中已安装的应用
		if (pi != null) {
			boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
			boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
			isSystemApp = isSysApp || isSysUpd;
		}
		return isSystemApp;
	}

	private void readTestFileTime() {
		String line = null;
		BufferedReader br = null;
		try {
			String path = Environment.getExternalStorageDirectory() + "/Android/data/";
//			String path = fullPath;
			/**
			 * $ ls -llcp /sdcard/Android/data/com.example.mytester/cache
			 * total 4
			 * -rw-rw---- 1 u0_a2058 sdcard_rw    0 2020-08-20 14:42:18.003563924 +0800 sdg.txt
			 * drwxrwx--x 2 u0_a2058 sdcard_rw 4096 2020-08-20 15:27:57.773562878 +0800 test/
			 */
			Process p = Runtime.getRuntime().exec("ls -llcp " + path);
//			Process p = Runtime.getRuntime().exec("stat " + "/sdcard/Android/data/com.android.browser");
			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
//				Log.i(FileWatcher.TAG, "shell: " + line);
				if (!TextUtils.isEmpty(line)) {
					// 不能简单使用" "分割，line中有可能得到多个连续空格，导致数组中包含多个空字符串，结果出错
					String[] arr = line.split("[ ]+");
					int len = 0;
					// 过滤掉第一行的"total 4"
					if (arr != null && (len = arr.length) > 2) {
						String file = findFileName(arr);
						// 不能从后往前找，有些文件名带有空格，会导致找错
//						String ctimeNano = arr[len - 4] + " " + arr[len - 3];
						String ctimeNano = arr[5] + " " + arr[6];
						long ctimeMillis = nano2Millis(ctimeNano);
						Log.i(FileWatcher.TAG, "file: " + file + ", ctimeNano: " + ctimeNano + ", ctimeMillis: " + ctimeMillis
								+ ", ctimeMillisF: " + formatTimestamp(ctimeMillis));
					}
				}
			}
		} catch (Throwable t) {
			Log.e(FileWatcher.TAG, t.getMessage(), t);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Throwable tt) {}
			}
		}
	}

	private long nano2Millis(String nano) {
		long millis = 0;
		if (!TextUtils.isEmpty(nano)) {
			try {
				String millisStr = nano.substring(0, nano.length() - 6);
				Log.i(FileWatcher.TAG, "millisStr: " + millisStr);
				millis = parse2Timestamp(millisStr);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return millis;
	}

	private long parse2Timestamp(String dateTime) {
		long time = 0;
		try {
			DateFormat df = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
			Date date = df.parse(dateTime);
			time = date.getTime();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return time;
	}

	private String formatTimestamp(long timestamp) {
		String dateTime = "";
		try {
			DateFormat df = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
			Date date = new Date(timestamp);
			dateTime = df.format(date);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return dateTime;
	}

	private String findFileName(String[] arr) {
		String fileName = "";
		int len = 0;
		if (arr != null && (len = arr.length) >= 9) {
			try {
				for (int start = 8; start < len; start++) {
					fileName += arr[start];
				}
			} catch (Throwable t) {
				MobLog.getInstance().d(t);
			}
		}
		return fileName;
	}
}
