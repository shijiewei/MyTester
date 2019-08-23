package com.example.weishj.mytester.ui;

import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileShareActivity extends BaseActivity {
	private static final String TAG = "FileShareActivity";
	// /sdcard
	private static final String TEST_PATH_SDCARD = Environment.getExternalStorageDirectory() + "/AMyTest/";
	private static final String TEST_CONTENT = "This is content";
	// /sdcard/Android/data/<pkgName>/files
	private String TEST_PATH_PRIVATE_DIR;
	private static final String TEST_NAME = ".test";
	private static final String TEST_DISPLAY_NAME = "test_img";
	private static final String TEST_ASSET_IMG = "testImg.jpg";
	private static final String TEST_CREATED_IMG_NAME = "to_test.jpg";
	private static final String TEST_CREATED_FILE_SAF = ".txt";
	private static final int REQUEST_DELETE_OTHERS_FILE = 1001;
	private static final int REQUEST_CREATE_FILE_BY_SAF = 1002;
	private String createFileUri;
	private String safCreatedFileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 传null指访问默认的files目录，传Environment.DIRECTORY_DCIM就是/files/DCIM/目录
		TEST_PATH_PRIVATE_DIR = this.getExternalFilesDir(null) + "/AMyTest/";
		setContentView(R.layout.activity_file_share);
		initView();
	}

	private void initView() {
		findViewById(R.id.page_test_create_file_by_file_sdcard_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_file_sdcard_btn).setOnClickListener(this);
		findViewById(R.id.page_test_create_file_by_file_private_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_file_private_btn).setOnClickListener(this);
		findViewById(R.id.page_test_create_file_by_media_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_media_btn).setOnClickListener(this);
		findViewById(R.id.page_test_create_file_by_content_resolver_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_content_resolver_btn).setOnClickListener(this);
		findViewById(R.id.page_test_delete_others_file_btn).setOnClickListener(this);
		findViewById(R.id.page_test_create_file_by_saf_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_saf_btn).setOnClickListener(this);
	}

	@Override
	protected void onViewClicked(View v) {
		int id = v.getId();
		if (id == R.id.page_test_create_file_by_file_sdcard_btn) {
			createFileByFileAPI(TEST_PATH_SDCARD, TEST_NAME);
		} else if (id == R.id.page_test_read_file_by_file_sdcard_btn) {
			readFileByFileAPI(TEST_PATH_SDCARD, TEST_NAME);
		} else if (id == R.id.page_test_create_file_by_file_private_btn) {
			createFileByFileAPI(TEST_PATH_PRIVATE_DIR, TEST_NAME);
		} else if (id == R.id.page_test_read_file_by_file_private_btn) {
			readFileByFileAPI(TEST_PATH_PRIVATE_DIR, TEST_NAME);
		} else if (id == R.id.page_test_create_file_by_media_btn) {
			createFileByMedia(TEST_ASSET_IMG, TEST_CREATED_IMG_NAME, "test img save use media");
		} else if (id == R.id.page_test_read_file_by_media_btn) {
			readFileByMedia(TEST_CREATED_IMG_NAME);
		} else if (id == R.id.page_test_create_file_by_content_resolver_btn) {
			createFileUri = createFileByContentResolver(this, "image/jpeg",
					TEST_DISPLAY_NAME, "test img save use insert");
		} else if (id == R.id.page_test_read_file_by_content_resolver_btn) {
			readFileByContentResolver(this, TEST_DISPLAY_NAME + ".jpg");
		} else if (id == R.id.page_test_delete_others_file_btn) {
			deleteOthersFile(this, TEST_CREATED_IMG_NAME);
		} else if (id == R.id.page_test_create_file_by_saf_btn) {
			createFileBySAF("text/plain", TEST_CREATED_FILE_SAF);
		} else if (id == R.id.page_test_read_file_by_saf_btn) {
			readFileBySAF(safCreatedFileName);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult.\nrequestCode: " + requestCode + "\nresultCode" + resultCode);
		if (requestCode == REQUEST_DELETE_OTHERS_FILE) {
			if (resultCode == RESULT_OK) {
				// data=null，拿不到其他什么信息
				log("User granted to delete others file");
				deleteOthersFile(this, TEST_CREATED_IMG_NAME);
			} else if (resultCode == RESULT_CANCELED) {
				log("Delete others file failed.\nUser refused");
			}
		} else if (requestCode == REQUEST_CREATE_FILE_BY_SAF) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					safCreatedFileName = writeStringIntoURI(TEST_CONTENT, uri);
				}
			} else if (resultCode == RESULT_CANCELED) {
				log("Create file by saf failed.\nUser canceled");
			}
		}
	}

	private void createFileByFileAPI(String path, String name) {
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
				log("File created. " + file.getAbsolutePath());
			} else {
				log("File already exists. " + file.getAbsolutePath());
			}
		} catch (IOException e) {
			log("FAILED\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void readFileByFileAPI(String path, String name) {
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
				log("File read successfully: " + sb.toString());
			} catch (Throwable e) {
				log("File read failed. " + file);
				e.printStackTrace();
			}
		} else {
			log("File does not exist. " + file);
		}
	}

	private void log(String msg) {
		Log.d(TAG, msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void createFileByMedia(String originImg, String displayName, String desc) {
		try {
			// 字符串的InputStream无法通过decodeStream转换成Bitmap
//			InputStream inputStream = new ByteArrayInputStream(TEST_CONTENT.getBytes());
			InputStream inputStream = this.getResources().getAssets().open(originImg);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			String uri = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, displayName, desc);
			log("Create img by media success. uri: " + uri);
		} catch (Throwable e) {
			log("Create img by media failed.\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void readFileByMedia(String name) {
		/**
		 * 通过ContentProvider查询文件，获得需要读取的文件Uri
		 */
		List<Uri> photoUris = new ArrayList<>();
		Cursor cursor = this.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
				, MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{name}, null);
		Log.e(TAG, "cursor size:" + cursor.getCount());
		Uri photoUri = null;
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Images.Media._ID));
			photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
			String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
			Log.e(TAG, "photoUri:" + photoUri +  ", displayName: " + displayName);
			photoUris.add(photoUri);
		}
		cursor.close();

		// 测试文件是img，用以下代码转成string后是乱码
		/**
		 * 通过Uri读取文件
		 */
//		try {
//			ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(photoUri, "r");
//			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//			FileInputStream fis = new FileInputStream(fileDescriptor);
//			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
//			BufferedReader br = new BufferedReader(isr);
//			StringBuffer sb = new StringBuffer();
//			String line = br.readLine();
//			while (line != null) {
//				if (sb.length() > 0) {
//					sb.append("\n");
//				}
//				sb.append(line);
//				line = br.readLine();
//			}
//			br.close();
//			log("File read successfully: " + sb.toString());
//		} catch (Throwable e) {
//			log(e.getMessage());
//			e.printStackTrace();
//		}

		/**
		 * 通过uri还原Bitmap
		 */
		try {
			ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(photoUri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			parcelFileDescriptor.close();
			log("Read file by media success.");
		} catch (Throwable t) {
			t.printStackTrace();
			log("Read file by media failed.\n" + t.getMessage());
		}
	}

	/**
	 * 在/sdcard/Pictures/下创建图片
	 *
	 * @param context
	 * @param mimeType
	 * @param name 插入时，文件名不用带后缀，系统会根据mimetype自动加上后缀
	 * @param description
	 * @return
	 */
	private String createFileByContentResolver(Context context, String mimeType,
											   String name, String description) {
		ContentValues values = new ContentValues();
		/*
		 * name="" 或 name=null，系统自动加上时间戳（157424667424.jpg）
		 * name=.no，系统自动加上下划线（_.n.jpg）
		 */
		values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
		values.put(MediaStore.Images.Media.DESCRIPTION, description);
		values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
		Uri url = null;
		String stringUrl = null;    /* value to be returned */
		ContentResolver cr = context.getContentResolver();
		try {
			url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			if (url == null) {
				return null;
			}
			byte[] buffer = new byte[1024];
			ParcelFileDescriptor parcelFileDescriptor = cr.openFileDescriptor(url, "w");
			FileOutputStream fileOutputStream =
					new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
			// 读取asset下的图片，写入文件中
//			InputStream inputStream = context.getResources().getAssets().open(saveFileName);
//
//			while (true) {
//				int numRead = inputStream.read(buffer);
//				if (numRead == -1) {
//					break;
//				}
//				fileOutputStream.write(buffer, 0, numRead);
//			}
			OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream, "utf-8");
			osw.append(TEST_CONTENT);
			fileOutputStream.flush();
			osw.flush();
			osw.close();
		} catch (Exception e) {
			log("Failed to insert media file.\n" + e.getMessage());
			e.printStackTrace();
			if (url != null) {
				cr.delete(url, null, null);
				url = null;
			}
		}
		if (url != null) {
			stringUrl = url.toString();
			log("Media file inserted.");
		}
		return stringUrl;
	}

	/**
	 * 查看/sdcard/Pictures/下的图片文件
	 *
	 * @param context
	 * @param name 查找时，文件名应该是带有后缀的全称，否则找不到文件
	 */
	private void readFileByContentResolver(Context context, String name) {
		/**
		 * 通过ContentProvider查询文件，获得需要读取的文件Uri
 		 */
		List<Uri> photoUris = new ArrayList<>();
		// EXTERNAL_CONTENT_URI: 存储在SD卡上的多媒体文件ContentProvider的URI
		// INTERNAL_CONTENT_URI: 存储在system下的多媒体文件ContentProvider的URI
		/*
		 uri：这个Uri代表要查询的数据库名称加上表的名称。这个Uri一般都直接从MediaStore里取得，例如我要取所有歌的信息，就必须利用MediaStore.Audio.Media.EXTERNAL_CONTENT_URI这个Uri。专辑信息要利用MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI这个Uri来查询，其他查询也都类似。
		 prjs：这个参数代表要从表中选择的列，用一个String数组来表示。
		 selections：相当于SQL语句中的where子句，就是代表你的查询条件。
		 selectArgs：这个参数是说你的Selections里有？这个符号是，这里可以以实际值代替这个问号。如果Selections这个没有？的话，那么这个String数组可以为null。
		 order：说明查询结果按什么来排序。
		 */
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
				, MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{name}, null);
		Log.e(TAG, "cursor size:" + cursor.getCount());
		Uri photoUri = null;
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Images.Media._ID));
			photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
			String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
			Log.e(TAG, "photoUri:" + photoUri +  ", displayName: " + displayName);
			photoUris.add(photoUri);
		}
		cursor.close();

		/**
		 * 通过Uri读取文件
 		 */
		try {
			ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(photoUri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			FileInputStream fis = new FileInputStream(fileDescriptor);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			log("File read successfully: " + sb.toString());
		} catch (Throwable e) {
			log(e.getMessage());
			e.printStackTrace();
		}

		/**
		 * 通过uri还原Bitmap
		 */
//		ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(photoUri, "r");
//		FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//		Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//		parcelFileDescriptor.close();
	}

	/**
	 * 删除共享集合目录下的媒体文件
	 *
	 * @param context
	 * @param name 查找时，文件名应该是带有后缀的全称，否则找不到文件
	 */
	private void deleteOthersFile(Context context, String name) {
		/**
		 * 通过ContentProvider查询文件，获得需要读取的文件Uri
		 */
		List<Uri> photoUris = new ArrayList<>();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
				, MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{name}, null);
//		Cursor cursor = context.getContentResolver().query(
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
//				, null, null, null);
		Log.e(TAG, "cursor size:" + cursor.getCount());
		Uri photoUri = null;
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Images.Media._ID));
			photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
			String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
			Log.e(TAG, "photoUri:" + photoUri +  ", displayName: " + displayName);
			photoUris.add(photoUri);
		}
		cursor.close();

		/**
		 * 通过Uri删除文件
		 */
		if (Build.VERSION.SDK_INT >= 29) {
			try {
//			context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{name});
				// ContentResolver.delete() 方法只是删除了媒体数据库中的数据，并不能删除共享集合目录（/Picture）下的图片
				context.getContentResolver().delete(photoUri, null, null);

				// 即便用户授权后，以下的删除代码也不能删除共享集合目录（/Picture）下的图片
//				File file = new File("/sdcard/Pictures", TEST_CREATED_IMG_NAME);
//				if (file != null && file.exists()) {
//					file.delete();
//				}
				log("Delete others file successfully. file: " + name);
			} catch (Throwable t) {
				if (t instanceof RecoverableSecurityException) {
					RecoverableSecurityException rse = (RecoverableSecurityException) t;
					IntentSender requestAccessIntentSender = rse.getUserAction()
							.getActionIntent().getIntentSender();
					// In your code, handle IntentSender.SendIntentException.
					try {
						startIntentSenderForResult(requestAccessIntentSender, REQUEST_DELETE_OTHERS_FILE,
								null, 0, 0, 0, null);
					} catch (IntentSender.SendIntentException e) {
						log(rse.getMessage());
						e.printStackTrace();
					}
				} else {
					log(t.getMessage());
					t.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 * // Here are some examples of how you might call this method.
	 * // The first parameter is the MIME type, and the second parameter is the name
	 * // of the file you are creating:
	 * //
	 * // createFile("text/plain", "foobar.txt");
	 * // createFile("image/png", "mypicture.png");
	 *
	 * @param mimeType
	 * @param fileName
	 */
	private void createFileBySAF(String mimeType, String fileName) {
		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

		// Filter to only show results that can be "opened", such as
		// a file (as opposed to a list of contacts or timezones).
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		// Create a file with the requested MIME type.
		intent.setType(mimeType);
		intent.putExtra(Intent.EXTRA_TITLE, fileName);
		startActivityForResult(intent, REQUEST_CREATE_FILE_BY_SAF);
	}

	private String writeStringIntoURI(String content, Uri uri) {
		String displayName = "";
		try {
			ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "w");
			FileOutputStream fileOutputStream =
					new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
			OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream, "utf-8");
			osw.append(content);
			fileOutputStream.flush();
			osw.flush();
			osw.close();
			displayName = getFileNameFromUri(uri, null);
			log("File created. file: " + displayName);
		} catch (Exception e) {
			log("Failed to write string into uri.\n" + e.getMessage());
			e.printStackTrace();
		}
		return displayName;
	}

	private String getFileNameFromUri(Uri uri, String selection) {
		String path = null;
		Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
		if (cursor != null) {
			Log.e(TAG, "cursor size:" + cursor.getCount());
			if (cursor.moveToNext()) {
				// Q 开始，已经无法通过MediaStore.Images.Media.DATA直接获取文件在sdcard的真实路径了
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
			}

			cursor.close();
		}
		return path;
	}

	private void readFileBySAF(String name) {
		if (TextUtils.isEmpty(name)) {
			log("请先使用SAF创建一个文件，再读取");
			return;
		} else {
			Log.d(TAG, "readFileBySAF. name: " + name);
		}
		if (Build.VERSION.SDK_INT >= 29) {
			try {
				/**
				 * 通过ContentProvider查询文件，获得需要读取的文件Uri
				 *
				 * 注意：无法通过以下方式获取/Download文件夹下的文件
				 */
//				List<Uri> photoUris = new ArrayList<>();
//				Cursor cursor = this.getContentResolver().query(
//						MediaStore.Downloads.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME}
//						, null, null, null);
//				Log.e(TAG, "cursor size:" + cursor.getCount());
//				Uri uri = null;
//				while (cursor.moveToNext()) {
//					int id = cursor.getInt(cursor
//							.getColumnIndex(MediaStore.Downloads._ID));
//					uri = Uri.parse(MediaStore.Downloads.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
//					String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME));
//					Log.e(TAG, "photoUri:" + uri +  ", displayName: " + displayName);
//					photoUris.add(uri);
//				}
//				cursor.close();

				Uri uri = null;
				if (uri != null) {
					// 从 URI 中获取 InputStream，写入字符串
					InputStream inputStream = getContentResolver().openInputStream(uri);
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							inputStream));
					StringBuilder stringBuilder = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
					}
					inputStream.close();
					reader.close();
					log("Read file by SAF successfully.\n" + stringBuilder.toString());
				} else {
					log("File not found. file: " + name);
				}
			} catch (Throwable t) {
				log("Read file by SAF failed.\n" + t.getMessage());
				t.printStackTrace();
			}
		} else {
			log("API level " + Build.VERSION.SDK_INT + ", no need to read by saf");
		}
	}
}
