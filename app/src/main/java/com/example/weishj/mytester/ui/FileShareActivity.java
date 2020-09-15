package com.example.weishj.mytester.ui;

import android.app.RecoverableSecurityException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;
import com.example.content.CustomizedFileProvider;
import com.example.weishj.mytester.provider.DocumentFile;
import com.example.weishj.mytester.util.Const;
import com.example.weishj.mytester.util.DateUtils;
import com.example.weishj.mytester.util.UriUtils;

import java.io.BufferedReader;
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

/**
 * 测试Android Q 分区存储机制下的文件操作与应用间共享
 *
 * 说明：
 * 1. 由于是测试应用间的文件共享，为了少写代码，采用同一个工程，编译不同包名的apk进行测试。请将该项目分别编译以下两个applicationId的apk：
 *		（1）com.example.mytester
 *		（2）com.example.fake
 */
public class FileShareActivity extends BaseActivity {
	private static final String TAG = "FileShareActivity";
	private static final String FILE_PROVIDER_NAME = "mob_file_provider";
	private static final String TEST_CREATED_DIR = "AMyDir";
	private static final String TEST_CREATED_FILE = ".txt";
	// /sdcard
	private static final String TEST_PATH_SDCARD = Environment.getExternalStorageDirectory() + "/" + TEST_CREATED_DIR;
	private static final String TEST_NAME = ".test";
	private static final String TEST_CREATED_FILE_NAME = "from_string";
	private static final String TEST_ASSET_IMG = "testImg.jpg";
	private static final String TEST_CREATED_IMG_NAME = "from_assets.jpg";
	private static final String TEST_CREATED_FILE_SAF = ".txt";
	private static final String EXTERNAL_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	private static final String EXTERNAL_DOWNLOAD = EXTERNAL_ROOT + "/Download";
	private static final String EXTERNAL_PICTURES = "/Pictures";
	private static final int REQUEST_DELETE_OTHERS_FILE_MEDIA_STORE = 1000;
	private static final int REQUEST_DELETE_OTHERS_FILE_CONTENT_RESOLVER = 1001;
	private static final int REQUEST_CREATE_FILE_BY_SAF = 1002;
	private static final int REQUEST_READ_FILE_BY_SAF = 1003;
	private static final int REQUEST_EDIT_FILE_BY_SAF = 1004;
	private static final int REQUEST_APPLY_DIR_AUTH = 1005;
	private static final int REQUEST_OPEN_ACTIVITY = 1006;
	private String FILE_PROVIDER_WHOLE_URI_FAKE;
	private String FILE_FPOVIDER_AUTHORITY_FAKE;
	private String FILE_PROVIDER_WHOLE_URI_MYSELF;
	private String FILE_FPOVIDER_AUTHORITY_MYSELF;
	private static String TEST_CONTENT;
	// /sdcard/Android/data/<pkgName>/files
	private String TEST_PATH_UNDER_PRIVATE_DIR;
	private String TEST_PATH_OTHERS_PRIVATE_DIR;
	private String PRIVATE_DIR_MYSELF;
	private String PRIVATE_DIR_OTHERS;
	private String contentResolverCreateFileName;
	private String safCreatedFileName;
	private String mediaCreatedFileName;
	private Uri sharableUri;
	private String myPkg;
	private String fakePkg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_share);

		myPkg = this.getApplicationContext().getPackageName();
		// 传null指访问默认的files目录，传Environment.DIRECTORY_DCIM就是/files/DCIM/目录
		TEST_PATH_UNDER_PRIVATE_DIR = this.getExternalFilesDir(null) + "/" + TEST_CREATED_DIR;
		TEST_CONTENT = "Hello, " + myPkg;

		if ("com.example.mytester".equals(myPkg)) {
			fakePkg = "com.example.fake";
		} else {
			fakePkg = "com.example.mytester";
		}

		// 查询当前设备中有哪些应用注册了我们的CustomizedFileProvider（不包含自己）
		List<String> mobPkgs = queryInstalledMobPkg(this);

		// 测试只用两个应用相互共享，因此这里简单的取index=0的，不做循环处理
		if (mobPkgs != null && !mobPkgs.isEmpty()) {
			FILE_FPOVIDER_AUTHORITY_FAKE = mobPkgs.get(0) + "." + FILE_PROVIDER_NAME;
		}
		// 模拟本地硬编码其他应用共享的文件uri，通过FileProvider读取文件，这里的uri规则必须与/res/xml/filepaths.xml中定义的值匹配
		FILE_PROVIDER_WHOLE_URI_FAKE = "content://" + FILE_FPOVIDER_AUTHORITY_FAKE + "/" + Const.SHARABLE_PATH_ALIAS + "/" + TEST_NAME;
		FILE_FPOVIDER_AUTHORITY_MYSELF = myPkg + "." + FILE_PROVIDER_NAME;
		FILE_PROVIDER_WHOLE_URI_MYSELF = "content://" + FILE_FPOVIDER_AUTHORITY_MYSELF + "/" + Const.SHARABLE_PATH_ALIAS + "/" + TEST_NAME;
		TEST_PATH_OTHERS_PRIVATE_DIR = "/Android/data/" + fakePkg + "/" + TEST_CREATED_DIR;
		PRIVATE_DIR_MYSELF = "/Android/data/" + myPkg;
		PRIVATE_DIR_OTHERS = "/Android/data/" + fakePkg;

		initView();
		// 在私有目录下创建私有文件，用于测试跨进程访问私有目录
		createFileByFileAPI(this.getExternalFilesDir(null).getParent(), TEST_NAME, TEST_CONTENT, false);
		prepareShareByFileProvider();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setShareResult();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setShareResult();
	}

	private void initView() {
		String title = getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.title_file_share);
		((TextView)findViewById(R.id.id_file_share_title_tv)).setText(title);

		/** ==============File API操作================= */
		findViewById(R.id.page_test_create_file_by_file_sdcard_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_file_sdcard_btn).setOnClickListener(this);
		findViewById(R.id.page_test_create_file_by_file_private_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_file_private_btn).setOnClickListener(this);
		/** ==============MediaStore(ContentResolver)操作媒体目录================= */
		findViewById(R.id.page_test_create_file_by_media_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_media_btn).setOnClickListener(this);
		findViewById(R.id.page_test_delete_file_by_media_btn).setOnClickListener(this);
		findViewById(R.id.page_test_create_file_by_content_resolver_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_content_resolver_btn).setOnClickListener(this);
		findViewById(R.id.page_test_delete_file_by_content_resolver_btn).setOnClickListener(this);
		findViewById(R.id.page_test_create_video_by_content_resolver_btn).setOnClickListener(this);
		/** ==============SAF访问指定文件================= */
		findViewById(R.id.page_test_create_file_by_saf_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_saf_btn).setOnClickListener(this);
		findViewById(R.id.page_test_edit_file_by_saf_btn).setOnClickListener(this);
		/** ==============申请指定目录完整访问权，DocumentFile API操作================= */
		findViewById(R.id.page_test_apply_root_authorization_btn).setOnClickListener(this);
		findViewById(R.id.page_test_write_root_btn).setOnClickListener(this);
		findViewById(R.id.page_test_release_root_authorization_btn).setOnClickListener(this);
		findViewById(R.id.page_test_apply_download_authorization_btn).setOnClickListener(this);
		findViewById(R.id.page_test_write_download_btn).setOnClickListener(this);
		findViewById(R.id.page_test_release_download_authorization_btn).setOnClickListener(this);
		findViewById(R.id.page_test_write_others_private_dir_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_others_private_dir_btn).setOnClickListener(this);
		findViewById(R.id.page_test_write_media_dir_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_media_dir_by_media_store_btn).setOnClickListener(this);
		findViewById(R.id.page_test_release_all_authorization_btn).setOnClickListener(this);
		/** ==============FileProvider共享文件================= */
		findViewById(R.id.page_test_grant_file_provider_btn).setOnClickListener(this);
		findViewById(R.id.page_test_release_file_provider_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_file_provider_btn).setOnClickListener(this);
		findViewById(R.id.page_test_edit_file_by_file_provider_btn).setOnClickListener(this);
		findViewById(R.id.page_test_read_file_by_file_provider_with_intent_btn).setOnClickListener(this);
	}

	@Override
	protected void onViewClicked(View v) {
		int id = v.getId();
		if (id == R.id.page_test_create_file_by_file_sdcard_btn) {
			createFileByFileAPI(TEST_PATH_SDCARD, TEST_NAME, TEST_CONTENT, true);
		} else if (id == R.id.page_test_read_file_by_file_sdcard_btn) {
			readFileByFileAPI(TEST_PATH_SDCARD, TEST_NAME);
		} else if (id == R.id.page_test_create_file_by_file_private_btn) {
			createFileByFileAPI(TEST_PATH_UNDER_PRIVATE_DIR, TEST_NAME, TEST_CONTENT, true);
		} else if (id == R.id.page_test_read_file_by_file_private_btn) {
			readFileByFileAPI(TEST_PATH_UNDER_PRIVATE_DIR, TEST_NAME);
		} else if (id == R.id.page_test_create_file_by_media_btn) {
			mediaCreatedFileName = createImageByMedia(TEST_ASSET_IMG, TEST_CREATED_IMG_NAME,
					"test img save use media");
		} else if (id == R.id.page_test_read_file_by_media_btn) {
			String file = TextUtils.isEmpty(mediaCreatedFileName) ? TEST_CREATED_IMG_NAME : mediaCreatedFileName;
			readImageByContentResolver(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, file);
		} else if (id == R.id.page_test_delete_file_by_media_btn) {
			// 删除MediaStore创建的图片
			deleteOthersFile(this, REQUEST_DELETE_OTHERS_FILE_MEDIA_STORE, TEST_CREATED_IMG_NAME);
		} else if (id == R.id.page_test_create_file_by_content_resolver_btn) {
			contentResolverCreateFileName = createFileByContentResolver(this, "image/jpeg",
					TEST_CREATED_FILE_NAME, "test img save use insert");
		} else if (id == R.id.page_test_read_file_by_content_resolver_btn) {
			String file = TextUtils.isEmpty(contentResolverCreateFileName) ?
					TEST_CREATED_FILE_NAME + ".jpg" : contentResolverCreateFileName;
			readFileByContentResolver(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, file);
		} else if (id == R.id.page_test_delete_file_by_content_resolver_btn) {
			// 删除ContentResolver创建的文件
			deleteOthersFile(this, REQUEST_DELETE_OTHERS_FILE_CONTENT_RESOLVER,
					TEST_CREATED_FILE_NAME + ".jpg");
		} else if (id == R.id.page_test_create_video_by_content_resolver_btn) {
			createFileByContentResolver(this, "video/mp4",
					"video_from_asset", "test video save use insert");
		} else if (id == R.id.page_test_create_file_by_saf_btn) {
			createFileBySAF("text/plain", TEST_CREATED_FILE_SAF);
		} else if (id == R.id.page_test_read_file_by_saf_btn) {
			String file = TextUtils.isEmpty(safCreatedFileName) ? TEST_CREATED_FILE_SAF : safCreatedFileName;
			readFileBySAF(file);
		} else if (id == R.id.page_test_edit_file_by_saf_btn) {
			editFileBySAF();
		} else if (id == R.id.page_test_apply_root_authorization_btn) {
			applyDirPermission(this, EXTERNAL_ROOT);
		}
		else if (id == R.id.page_test_write_root_btn) {
			// 检查外部存储的根目录是否有权限
			Uri uri = checkDirPermission(this, EXTERNAL_ROOT);
			if (uri != null) {
				// 有权限，则通过根目录uri，在其下任意子目录写文件
				createFileByRootUri(this, uri, TEST_CREATED_DIR);
			} else {
				log("Please apply permission first.");
			}
		} else if (id == R.id.page_test_release_root_authorization_btn) {
			Uri uri = checkDirPermission(this, EXTERNAL_ROOT);
			// 有权限
			if (uri != null) {
				releaseDirPermission(this, uri);
			}
		} else if (id == R.id.page_test_apply_download_authorization_btn) {
			applyDirPermission(this, EXTERNAL_DOWNLOAD);
		} else if (id == R.id.page_test_write_download_btn) {
			// 检查指定目录是否有权限
			Uri uri = checkDirPermission(this, EXTERNAL_DOWNLOAD);
			if (uri != null) {
				// 有权限，则在通过该目录的uri，在其下写文件
				createFileUnderDir(this, uri);
			} else {
				log("Please apply permission first.");
			}
		} else if (id == R.id.page_test_release_download_authorization_btn) {
			Uri uri = checkDirPermission(this, EXTERNAL_DOWNLOAD);
			// 有权限
			if (uri != null) {
				releaseDirPermission(this, uri);
			}
		} else if (id == R.id.page_test_write_others_private_dir_btn) {
			Uri uri = checkDirPermission(this, EXTERNAL_ROOT);
			if (uri != null) {
				createFileByRootUri(this, uri, TEST_PATH_OTHERS_PRIVATE_DIR);
			} else {
				log("Please apply Root dir permission first.");
			}
		} else if (id == R.id.page_test_read_others_private_dir_btn) {
			Uri uri = checkDirPermission(this, EXTERNAL_ROOT);
			if (uri != null) {
				readFileByRootUri(this, uri, PRIVATE_DIR_OTHERS + "/" + TEST_NAME);
			} else {
				log("Please apply Root dir permission first.");
			}
		} else if (id == R.id.page_test_write_media_dir_btn) {
			Uri uri = checkDirPermission(this, EXTERNAL_ROOT);
			if (uri != null) {
				createFileByRootUri(this, uri, EXTERNAL_PICTURES);
			} else {
				log("Please apply Root dir permission first.");
			}
		}
		else if (id == R.id.page_test_read_media_dir_by_media_store_btn) {
			/*
			 * 这只是用于测试是否可以通过MediaStore方式读取DocumentFile在媒体目录下创建的文件，
			 * 结果是不行（因为文件虽然创建了，但没有插入媒体数据库，所以无法查询到目标文件uri）
			 */
			readFileByContentResolver(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, TEST_CREATED_FILE);
		} else if (id == R.id.page_test_release_all_authorization_btn) {
			List<Uri> uris = getPermittedUri(this);
			for (Uri uri : uris) {
				releaseDirPermission(this, uri);
			}
		} else if (id == R.id.page_test_grant_file_provider_btn) {
			grantPermissionForFileProvider(sharableUri);
		} else if (id == R.id.page_test_release_file_provider_btn) {
			releasePermissionOfFileProvider(sharableUri);
		} else if (id == R.id.page_test_read_file_by_file_provider_btn) {
			readFileByFileProvider(FILE_PROVIDER_WHOLE_URI_FAKE);
		} else if (id == R.id.page_test_edit_file_by_file_provider_btn) {
			editFileByFileProvider(FILE_PROVIDER_WHOLE_URI_FAKE);
		} else if (id == R.id.page_test_read_file_by_file_provider_with_intent_btn) {
			readFileByFileProviderWithIntent();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult.\nrequestCode: " + requestCode + "\nresultCode" + resultCode);
		if (requestCode == REQUEST_DELETE_OTHERS_FILE_MEDIA_STORE) {
			if (resultCode == RESULT_OK) {
				// data=null，拿不到其他什么信息
				log("User granted to delete others file");
				deleteOthersFile(this, REQUEST_DELETE_OTHERS_FILE_MEDIA_STORE, TEST_CREATED_IMG_NAME);
			} else if (resultCode == RESULT_CANCELED) {
				log("Delete others file failed.\nUser refused");
			}
		} else if(requestCode == REQUEST_DELETE_OTHERS_FILE_CONTENT_RESOLVER) {
			if (resultCode == RESULT_OK) {
				// data=null，拿不到其他什么信息
				log("User granted to delete others file");
				deleteOthersFile(this,
						REQUEST_DELETE_OTHERS_FILE_CONTENT_RESOLVER, TEST_CREATED_FILE_NAME + ".jpg");
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
		} else if (requestCode == REQUEST_READ_FILE_BY_SAF) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					String result = readStringFromURI(this, uri);
					log("File read successfully.\n " + result);
				}
			} else if (resultCode == RESULT_CANCELED) {
				log("Read file by saf failed.\nUser canceled");
			}
		} else if (requestCode == REQUEST_EDIT_FILE_BY_SAF) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					appendStringIntoUri(this, TEST_CONTENT, uri);
				}
			} else if (resultCode == RESULT_CANCELED) {
				log("Edit file by saf failed.\nUser canceled");
			}
		} else if (requestCode == REQUEST_APPLY_DIR_AUTH) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					if (Build.VERSION.SDK_INT >= 19) {
						Uri uri = data.getData();
						// 保留该Uri的永久权限
						getContentResolver().takePersistableUriPermission(uri,
								Intent.FLAG_GRANT_READ_URI_PERMISSION |
										Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

						String path = UriUtils.getPathOfExternalStorageFromGrantedUri(uri);
						log("Granted. \nDir: " + path);
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
				log("Apply dir auth failed.\nUser canceled");
			}
		} else if (requestCode == REQUEST_OPEN_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					readFileByFileProvider(uri.toString());
				}
			} else if (resultCode == RESULT_CANCELED) {
				log("Read file by file provider failed.\nUser canceled");
			}
		}
	}

	/**
	 * File API 创建文件
	 *
	 * @param path 路径
	 * @param name 文件名
	 * @param content 待写入内容
	 * @param autoToast 是否弹toast提示（测试用，与业务无关）
	 * @return
	 */
	private String createFileByFileAPI(String path, String name, String content, boolean autoToast) {
		File file = new File(path, name);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			boolean rst = file.createNewFile();
			if (rst) {
				// 创建成功，写入内容
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
				osw.write(content);
				osw.write(" ");
				osw.write(DateUtils.getDefaultTime(System.currentTimeMillis()));
				osw.write("\n");
				osw.flush();
				osw.close();
				if (autoToast) {
					log("File created. " + file.getAbsolutePath());
				}
			} else {
				// 创建失败，可能已经存在
				if (autoToast) {
					log("File already exists. " + file.getAbsolutePath());
				}
				// 更新文件内容
				if (file.isFile()) {
					FileOutputStream fos = new FileOutputStream(file);
					OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
					osw.write(content);
					osw.write(" ");
					osw.write(DateUtils.getDefaultTime(System.currentTimeMillis()));
					osw.write("\n");
					osw.flush();
					osw.close();
				}
			}
			return file.getAbsolutePath();
		} catch (IOException e) {
			log("FAILED\n" + e.getMessage());
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * File API 读取文件
	 *
	 * @param path
	 * @param name
	 */
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

	/**
	 * MediaStore(封装了ContentResolver)创建媒体文件
	 *
	 * @param originImg 测试用原始图片
	 * @param displayName 新文件名
	 * @param desc 文件描述
	 * @return 创建的文件名
	 */
	private String createImageByMedia(String originImg, String displayName, String desc) {
		String fileName = "";
		try {
			// 字符串的InputStream无法通过decodeStream转换成Bitmap
//			InputStream inputStream = new ByteArrayInputStream(TEST_CONTENT.getBytes());
			InputStream inputStream = this.getResources().getAssets().open(originImg);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			String uri = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, displayName, desc);
			fileName = getFileNameFromUri(Uri.parse(uri));
			String path = UriUtils.getPath(this, Uri.parse(uri));
			String file = TextUtils.isEmpty(path) ? fileName : path;
			log("Create img by media success. \nFile: " + file);
		} catch (Throwable e) {
			log("Create img by media failed.\n" + e.getMessage());
			e.printStackTrace();
		}
		return fileName;
	}

	/**
	 * ContentResolver取图片
	 *
	 * @param name 文件的displayName
	 */
	private void readImageByContentResolver(Context context, Uri searchFrom, String name) {
		/**
		 * 通过ContentProvider查询文件，获得需要读取的文件Uri
		 */
		List<Uri> uris = new ArrayList<>();
		Cursor cursor = context.getContentResolver().query(
				searchFrom, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
				, MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{name}, null);
		Log.e(TAG, "cursor size:" + cursor.getCount());
		Uri uri = null;
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Images.Media._ID));
			uri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
			String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
			Log.e(TAG, "uri:" + uri +  ", displayName: " + displayName);
			uris.add(uri);
		}
		cursor.close();

		/**
		 * 通过uri还原Bitmap
		 */
		try {
			ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			parcelFileDescriptor.close();
			log("Read image success.");
		} catch (Throwable t) {
			t.printStackTrace();
			log("Read image failed.\n" + t.getMessage());
		}
	}

	/**
	 * ContentResolver创建文件（图片/视频）
	 *
	 * @param context
	 * @param mimeType
	 * @param name 插入时，文件名不用带后缀，系统会根据mimetype自动加上后缀
	 * @param description
	 * @return
	 */
	private String createFileByContentResolver(Context context, String mimeType,
											   String name, String description) {
		ContentResolver cr = context.getContentResolver();
		Uri uri = null;
		try {
			/** 向系统媒体库插入媒体文件元数据，并获取对应的uri */
			ContentValues values = new ContentValues();
			if (mimeType != null && mimeType.startsWith("video")) {
				/*
				 * name="" 或 name=null，系统自动加上时间戳（157424667424.jpg）
				 * name=.no，系统自动加上下划线（_.n.jpg）
				 */
				values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
				values.put(MediaStore.Video.Media.DESCRIPTION, description);
				values.put(MediaStore.Video.Media.MIME_TYPE, mimeType);
				uri = cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
			} else if (mimeType != null && mimeType.startsWith("image")) {
				/*
				 * name="" 或 name=null，系统自动加上时间戳（157424667424.jpg）
				 * name=.no，系统自动加上下划线（_.n.jpg）
				 */
				values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
				values.put(MediaStore.Images.Media.DESCRIPTION, description);
				values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
				uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				// 其他类型可以做扩展，此处默认都按照图片处理了
				values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
				values.put(MediaStore.Images.Media.DESCRIPTION, description);
				values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
				uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			}
			if (uri == null) {
				return null;
			}

			/** 通过uri，写入文件 */
			byte[] buffer = new byte[1024];
			ParcelFileDescriptor parcelFileDescriptor = cr.openFileDescriptor(uri, "w");
			FileOutputStream fileOutputStream =
					new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

			// 读取asset下的图片/视频，写入文件中
			String testFileName;
			if (mimeType != null && mimeType.startsWith("video")) {
				testFileName = "testVideo.mp4";
			} else if (mimeType != null && mimeType.startsWith("image")) {
				testFileName = "testImg.jpg";
			} else {
				testFileName = "testImg.jpg";
			}
			InputStream inputStream = context.getResources().getAssets().open(testFileName);
			while (true) {
				int numRead = inputStream.read(buffer);
				if (numRead == -1) {
					break;
				}
				fileOutputStream.write(buffer, 0, numRead);
			}
			fileOutputStream.flush();
			fileOutputStream.close();

			// 甚至也可以直接向jpg/mp4文件中写入任意字符串，只不过该jpg无法当做图片浏览
//			OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream, "utf-8");
//			osw.write(TEST_CONTENT);
//			osw.write(" ");
//			osw.write(DateUtils.getDefaultTime(System.currentTimeMillis()));
//			osw.write("\n");
//			fileOutputStream.flush();
//			osw.flush();
//			osw.close();
		} catch (Exception e) {
			log("Failed to insert media file.\n" + e.getMessage());
			e.printStackTrace();
			if (uri != null) {
				cr.delete(uri, null, null);
				uri = null;
			}
		}
		String fileName = "";
		if (uri != null) {
			fileName = getFileNameFromUri(uri);
			String path = UriUtils.getPath(this, uri);
			String file = TextUtils.isEmpty(path) ? fileName : path;
			log("Media file inserted. \nFile: " + file);
		}
		return fileName;
	}

	/**
	 * 查看/sdcard/Pictures/下的图片文件
	 *
	 * @param context
	 * @param name 查找时，文件名应该是带有后缀的全称，否则找不到文件
	 */
	private void readFileByContentResolver(Context context, Uri searchFrom, String name) {
		/**
		 * 通过ContentProvider查询文件，获得需要读取的文件Uri
 		 */
		List<Uri> uris = new ArrayList<>();
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
				searchFrom, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
				, MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{name}, null);
//		Cursor cursor = context.getContentResolver().query(
//				searchFrom, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
//				, null, null, null);
		Log.e(TAG, "cursor size:" + cursor.getCount());
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor
						.getColumnIndex(MediaStore.Images.Media._ID));
				Uri uri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
				String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				String path = UriUtils.getPath(context, uri);
				String file = TextUtils.isEmpty(path) ? displayName : path;
				Log.e(TAG, "uri:" + uri +  ", File: " + file);
				uris.add(uri);

				/**
				 * 通过Uri读取文件
				 */
				String content = readStringFromURI(context, uri);
				log(content);
			}
		} else {
			log("File not found. \nFile: " + name);
		}
		cursor.close();
	}

	/**
	 * 删除共享集合目录下的媒体文件（自己创建的/其他应用创建的）
	 *
	 * 注意：若删除其他应用创建的媒体文件，需要READ_EXTERNAL_STORAGE权限
	 *
	 * @param context
	 * @param requestCode
	 * @param name 查找时，文件名应该是带有后缀的全称，否则找不到文件
	 */
	private void deleteOthersFile(Context context, int requestCode, String name) {
		/**
		 * 通过ContentProvider查询文件，获得需要读取的文件Uri
		 */
		List<Uri> uris = new ArrayList<>();
		// 查询指定文件名
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
				, MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{name}, null);
		// 查询所有
//		Cursor cursor = context.getContentResolver().query(
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME}
//				, null, null, null);
		Log.e(TAG, "cursor size:" + cursor.getCount());
		if (cursor.getCount() == 0) {
			log("File not found. File: " + name);
			return;
		} else {
			Uri uri = null;
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor
						.getColumnIndex(MediaStore.Images.Media._ID));
				uri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
				String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				Log.e(TAG, "uri:" + uri +  ", displayName: " + displayName);
				uris.add(uri);
			}
			cursor.close();

			/**
			 * 通过Uri删除文件
			 */
			if (Build.VERSION.SDK_INT >= 29) {
				try {
//			context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{name});
					// ContentResolver.delete() 方法只是删除了媒体数据库中的数据，并不能删除共享集合目录（/Picture）下的图片
					context.getContentResolver().delete(uri, null, null);

					// 即便用户授权后，以下的删除代码也不能删除共享集合目录（/Picture）下的图片
//				File file = new File("/sdcard/Pictures", TEST_CREATED_IMG_NAME);
//				if (file != null && file.exists()) {
//					file.delete();
//				}
					log("Delete others file successfully. \nFile: " + name);
				} catch (Throwable t) {
					// 捕获RecoverableSecurityException，向用户申请write权限
					if (t instanceof RecoverableSecurityException) {
						RecoverableSecurityException rse = (RecoverableSecurityException) t;
						IntentSender requestAccessIntentSender = rse.getUserAction()
								.getActionIntent().getIntentSender();
						// In your code, handle IntentSender.SendIntentException.
						try {
							startIntentSenderForResult(requestAccessIntentSender, requestCode,
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
	}

	/**
	 * SAF创建文件
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

	/**
	 * SAF读取文件
	 * 
	 * @param name
	 */
	private void readFileBySAF(String name) {
		/**
		 * 使用DocumentUI检索文件，获取uri
		 */
		// ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
		// browser.
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

		// Filter to only show results that can be "opened", such as a
		// file (as opposed to a list of contacts or timezones)
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		// Filter to show only images, using the image MIME data type.
		// If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
		// To search for all documents available via installed storage providers,
		// it would be "*/*".
		intent.setType("*/*");

		startActivityForResult(intent, REQUEST_READ_FILE_BY_SAF);
	}

	/**
	 * SAF编辑文件
	 */
	private void editFileBySAF() {
		// ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
		// file browser.
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

		// Filter to only show results that can be "opened", such as a
		// file (as opposed to a list of contacts or timezones).
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		// Filter to show only text files by "text/plain".
		intent.setType("*/*");

		startActivityForResult(intent, REQUEST_EDIT_FILE_BY_SAF);
	}

	/**
	 * 通过uri写文件（覆盖）
	 * 
	 * @param content
	 * @param uri
	 * @return
	 */
	private String writeStringIntoURI(String content, Uri uri) {
		String displayName = "";
		try {
			ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "w");
			FileOutputStream fileOutputStream =
					new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
			OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream, "utf-8");
			osw.write(content);
			osw.write(" ");
			osw.write(DateUtils.getDefaultTime(System.currentTimeMillis()));
			osw.write("\n");
			osw.flush();
			// Let the document provider know you're done by closing the stream.
			osw.close();
			parcelFileDescriptor.close();
			displayName = getFileNameFromUri(uri);
			String path = UriUtils.getPath(this, uri);
			log("File written. \nFile: " + (TextUtils.isEmpty(path) ? displayName : path));
		} catch (Exception e) {
			log("Failed to write string into uri.\n" + e.getMessage());
			e.printStackTrace();
		}
		return displayName;
	}

	/**
	 * 通过uri读取文件内容
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	private String readStringFromURI(Context context, Uri uri) {
		try {
			ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
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
			Log.d(TAG, "File read successfully.\nContent:\n" + sb.toString());
			return sb.toString();
		} catch (Throwable e) {
			e.printStackTrace();
			return "File read failed. \n" + e.getMessage();
		}
	}

	/**
	 * 通过uri写文件（追加）
	 * 
	 * @param context
	 * @param content
	 * @param uri
	 * @return
	 */
	private String appendStringIntoUri(Context context, String content, Uri uri) {
		String originContent = readStringFromURI(context, uri);
		return writeStringIntoURI(originContent + "\n" + content, uri);
	}

	/**
	 * 申请指定目录的完整访问权限（管理文件组）
	 *
	 * @param context
	 * @param path
	 */
	private void applyDirPermission(Context context, String path) {
		if (Build.VERSION.SDK_INT >= 19) {
			Uri uri = checkDirPermission(context, path);
			Log.d(TAG, "Apply dir permission. path: " + path + ", uri: " + uri);
			if (uri == null) {
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
				startActivityForResult(intent, REQUEST_APPLY_DIR_AUTH);
			} else {
				String realPath = UriUtils.getPathOfExternalStorageFromGrantedUri(uri);
				log("Already granted, no need to apply. \nDir: " + realPath);
			}
		} else {
			log("API < 19, no need to ask permission");
		}
	}

	/**
	 * 通过指定目录的uri，在其下写文件
	 *
	 * @param context
	 * @param uri
	 */
	private void createFileUnderDir(Context context, Uri uri) {
		if (Build.VERSION.SDK_INT >= 19) {
			//for directory choose
			DocumentFile pickedDir = DocumentFile.fromTreeUri(this, uri);

			// Create directory
			DocumentFile myDir = pickedDir.findFile(TEST_CREATED_DIR);
			if (myDir != null && myDir.isDirectory()) {
				Log.d(TAG, "Found dir. dir: " + UriUtils.getPath(context, myDir.getUri()));
			} else {
				myDir = pickedDir.createDirectory(TEST_CREATED_DIR);
				Log.d(TAG, "Create dir. dir: " + UriUtils.getPath(context, myDir.getUri()));
			}
			//Create file
			if (myDir != null && myDir.exists()) {
				DocumentFile myFile = myDir.findFile(TEST_CREATED_FILE);
				if (myFile != null && myFile.isFile()) {
					Log.d(TAG, "Found file. file: " + UriUtils.getPath(context, myFile.getUri()));
				} else {
					myFile = myDir.createFile("text/plain", TEST_CREATED_FILE);
					Log.d(TAG, "Create file. file: " + UriUtils.getPath(context, myFile.getUri()));
				}
				appendStringIntoUri(context, TEST_CONTENT, myFile.getUri());
			}
		} else {
			log("API < 19, no need to ask permission");
		}
	}

	/**
	 * 通过获取到的sdcard根目录uri，在指定的subPath下创建文件
	 *
	 * @param context
	 * @param uri 根目录uri
	 * @param subPath 想要创建在哪个子目录下
	 */
	private void createFileByRootUri(Context context, Uri uri, String subPath) {
		if (Build.VERSION.SDK_INT >= 19) {
			//for directory choose
			DocumentFile pickedDir = DocumentFile.fromTreeUri(this, uri);

			// Loop create directory
			String[] subPaths = subPath.split("/");
			DocumentFile tmpDir = pickedDir;
			if (subPath != null && subPath.length() > 0) {
				for (String sub : subPaths) {
					if (!TextUtils.isEmpty(sub)) {
						DocumentFile subDir = tmpDir.findFile(sub);
						if (subDir != null && subDir.isDirectory()) {
							tmpDir = subDir;
							Log.d(TAG, "Found dir. dir: " + UriUtils.getPath(context, tmpDir.getUri()));
						} else {
							tmpDir = tmpDir.createDirectory(sub);
							Log.d(TAG, "Create dir. dir: " + UriUtils.getPath(context, tmpDir.getUri()));
						}
					}
				}
			}
			//Create file
			if (tmpDir != null && tmpDir.exists()) {
				DocumentFile myFile = tmpDir.findFile(TEST_CREATED_FILE);
				if (myFile != null && myFile.isFile()) {
					Log.d(TAG, "Found file. file: " + UriUtils.getPath(context, myFile.getUri()));
				} else {
					myFile = tmpDir.createFile("text/plain", TEST_CREATED_FILE);
					Log.d(TAG, "Create file. file: " + UriUtils.getPath(context, myFile.getUri()));
				}
				appendStringIntoUri(context, TEST_CONTENT, myFile.getUri());
			}
		} else {
			log("API < 19, no need to ask permission");
		}
	}

	/**
	 * 通过获取到的sdcard根目录uri，读取指定的subPath下的文件内容
	 *
	 * @param context
	 * @param uri 根目录uri
	 * @param rootRelativePath 相对于root的路径（包含文件名）
	 */
	private void readFileByRootUri(Context context, Uri uri, String rootRelativePath) {
		if (Build.VERSION.SDK_INT >= 19) {
			//for directory choose
			DocumentFile pickedDir = DocumentFile.fromTreeUri(this, uri);

			// Loop create directory
			String[] subPaths = rootRelativePath.split("/");
			DocumentFile tmpDir = pickedDir;
			if (rootRelativePath != null && rootRelativePath.length() > 0) {
				for (String sub : subPaths) {
					if (!TextUtils.isEmpty(sub)) {
						DocumentFile subDir = tmpDir.findFile(sub);
						if (subDir != null && subDir.isDirectory()) {
							tmpDir = subDir;
							Log.d(TAG, "Found dir. dir: " + UriUtils.getPath(context, tmpDir.getUri()));
						} else if (subDir != null && subDir.isFile()) {
							Uri fileUri = subDir.getUri();
							String content = readStringFromURI(context, fileUri);
							log("File: " + rootRelativePath + "\nContent:\n" + content);
						}
					}
				}
			}
		} else {
			log("API < 19, no need to ask permission");
		}
	}

	/**
	 * 通过路径uri撤销对该路径的授权
	 *
	 * @param context
	 * @param uri
	 */
	private void releaseDirPermission(Context context, Uri uri) {
		if (Build.VERSION.SDK_INT >= 19) {
			int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
			context.getContentResolver().releasePersistableUriPermission(uri, flags);
			String path = UriUtils.getPathOfExternalStorageFromGrantedUri(uri);
			log("Released. \nDir: " + path);
		}
	}

	/**
	 * 向指定Uri授予林是访问权限（用于FileProvider共享文件）
	 *
	 * @param uri
	 */
	private void grantPermissionForFileProvider(Uri uri) {
		if (sharableUri != null) {
			List<String> mobPkgs = queryInstalledMobPkg(this);
			if (mobPkgs != null && !mobPkgs.isEmpty()) {
				for (String pkg : mobPkgs) {
					/**
					 *
					 * 参数1 授予权限的app包名，如果不确定是哪个APP使用，就将所有查询出来符合的app都授权
					 * 参数2 授予权限的URi
					 * 参数3 授予的读写权限,这里可取 FLAG_GRANT_READ_URI_PERMISSION，FLAG_GRANT_WRITE_URI_PERMISSION,
					 * 或者都设置上.这个授权将在你调用revokeUriPermission()或者重启设置之前一直有效.
					 */
					grantUriPermission(pkg, uri,
							Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
					log("Grant to " + pkg);
				}
			}
		}
	}

	/**
	 * 撤销指定Uri的权限（用于FileProvider共享文件）
	 *
	 * @param uri
	 */
	private void releasePermissionOfFileProvider(Uri uri) {
		this.revokeUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		log("Release uri permission. \nuri: " + uri);
	}

	/**
	 * 本地硬编码其他应用共享的文件Uri，然后跨应用读取（用于FileProvider共享文件）
	 *
	 * @param uriStr
	 */
	private void readFileByFileProvider(String uriStr) {
		try {
			Uri uri = Uri.parse(uriStr);
			Log.d(TAG, "Read file from uri: " + uri);

			String displayName = getFileNameFromUri(uri);
			// Q开始无法通过该方法获取文件路径
			String path = UriUtils.getPath(this, uri);
			String file = TextUtils.isEmpty(path) ? displayName : path;
			String content = readStringFromURI(this, uri);
			log("File: " + file + "\nContent:\n" + content);
		} catch (Throwable t) {
			t.printStackTrace();
			log(t.getMessage());
		}
	}

	/**
	 * 编辑通过FileProvider共享的文件（用于FileProvider共享文件）
	 *
	 * @param uriStr
	 */
	private void editFileByFileProvider(String uriStr) {
		final Uri uri = Uri.parse(uriStr);
		Log.d(TAG, "Edit file by uri: " + uri);
		appendStringIntoUri(this, TEST_CONTENT, uri);

		// 测试多进程共同写一个文件，结果：a、b（几乎）同时写c共享的文件时，只有a或b会成功，另一个的数据会丢失。
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				int i = 0;
//				while (i < 100) {
//					String content = TEST_CONTENT + "/" + i;
//					Log.d("abcba", content);
//					appendStringIntoUri(getApplicationContext(), content, uri);
//					i ++;
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
	}

	/**
	 * 测试通过Intent传递uri的FileProvider共享方式
	 *
	 * 注：onActivityResult中暂未获取到intent，由于该方式不适合我们的业务场景，不再做深入研究
	 */
	private void readFileByFileProviderWithIntent() {
		// Activity的全路径是固定的，不需要根据包名区分
		String activity_path = "com.example.weishj.mytester.ui.FileShareActivity";
		ComponentName comp = new ComponentName(fakePkg, activity_path);
		Intent intent = new Intent();
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//可选
		intent.setComponent(comp);
		startActivityForResult(intent, REQUEST_OPEN_ACTIVITY);
	}

	/** =============================================================================================== */

	private void log(final String msg) {
		Log.d(TAG, msg);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void prepareShareByFileProvider() {
		// 生成测试用文件
		String path = createFileByFileAPI(this.getExternalFilesDir(null).getAbsolutePath() + "/MyPrivate",
				TEST_NAME, TEST_CONTENT, false);

		// 为文件生成Uri
		File file = new File(path);
		if (file != null && file.exists()) {
			sharableUri = CustomizedFileProvider.getUriForFile(this,FILE_FPOVIDER_AUTHORITY_MYSELF, file);
		}
		Log.d(TAG, "Sharable uri: " + sharableUri);

		// 授予URI一个临时的访问权限
		grantPermissionForFileProvider(sharableUri);
	}

	private void prepareFileUnderPrivateDir(String filePath, String content) {
		try {
			File file = new File(filePath);
			if (file != null && !file.exists()) {
				File parent = file.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
				boolean result = file.createNewFile();
				if (result) {

				} else {
					log("Create file failed");
				}
			}
		} catch (Throwable t) {
			log(t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * 这只是一个测试方法，用于测试使用ContentResolver读取/Downloads文件夹，结果是无法读取
	 *
	 * @param name
	 */
	private void readDownloadsByContentResolver(String name) {
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
				List<Uri> photoUris = new ArrayList<>();
				Cursor cursor = this.getContentResolver().query(
						MediaStore.Downloads.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME}
						, null, null, null);
				Log.e(TAG, "cursor size:" + cursor.getCount());
				Uri uri = null;
				while (cursor.moveToNext()) {
					int id = cursor.getInt(cursor
							.getColumnIndex(MediaStore.Downloads._ID));
					uri = Uri.parse(MediaStore.Downloads.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
					String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME));
					Log.e(TAG, "photoUri:" + uri +  ", displayName: " + displayName);
					photoUris.add(uri);
				}
				cursor.close();

				/**
				 * 通过uri读取文件
				 */
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

	/**
	 * 检查文档元数据
	 *
	 * @param uri
	 * @return displayName
	 */
	private String getFileNameFromUri(Uri uri) {
		String displayName = null;
		// The query, since it only applies to a single document, will only return
		// one row. There's no need to filter, sort, or select fields, since we want
		// all fields for one document.
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		try {
			// 获取文档的名称
			// moveToFirst() returns false if the cursor has 0 rows.  Very handy for
			// "if there's anything to look at, look at it" conditionals.
			if (cursor != null  && cursor.moveToFirst()) {
				Log.e(TAG, "cursor size:" + cursor.getCount());
				// Note it's called "Display Name".  This is
				// provider-specific, and might not necessarily be the file name.
				displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
			}

			// 获取文档的大小
			int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
			// If the size is unknown, the value stored is null.  But since an
			// int can't be null in Java, the behavior is implementation-specific,
			// which is just a fancy term for "unpredictable".  So as
			// a rule, check if it's null before assigning to an int.  This will
			// happen often:  The storage API allows for remote files, whose
			// size might not be locally known.
			String size = null;
			if (!cursor.isNull(sizeIndex)) {
				// Technically the column stores an int, but cursor.getString()
				// will do the conversion automatically.
				size = cursor.getString(sizeIndex);
			} else {
				size = "Unknown";
			}
			Log.e(TAG, "File: " + displayName + ", Size: " + size);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return displayName;
	}

	/**
	 * 获取应用的所有已授权Uri（用于管理文件组）
	 *
	 * @param context
	 * @return
	 */
	private List<Uri> getPermittedUri(Context context) {
		List<Uri> uris = new ArrayList<>();
		if (Build.VERSION.SDK_INT >= 19) {
			List<UriPermission> uriPermissions = context.getContentResolver().getPersistedUriPermissions();
			for (UriPermission uriPermission : uriPermissions) {
				uris.add(uriPermission.getUri());
			}
		}
		return uris;
	}

	/**
	 * 检查给定的目录是否具备访问权限（用于管理文件组）
	 *
	 * @param context
	 * @param root
	 * @return
	 */
	private Uri checkDirPermission(Context context, String root) {
		Uri tmpUri = null;
		if (Build.VERSION.SDK_INT >= 19) {
			tmpUri = null;
			List<Uri> uris = getPermittedUri(context);
			for (Uri uri : uris) {
				String path = UriUtils.getPathOfExternalStorageFromGrantedUri(uri);
				if (root.equals(path)) {
					tmpUri = uri;
					break;
				}
			}
			return tmpUri;
		} else {
			log("API < 19, no need to ask permission");
			return tmpUri;
		}
	}

	private void startApp(String pkgName) {
		PackageManager packageManager = this.getPackageManager();
		Intent it = packageManager.getLaunchIntentForPackage(pkgName);
		startActivityForResult(it, REQUEST_OPEN_ACTIVITY);
	}

	private void setShareResult() {
		Intent intent = new Intent();
		intent.setData(sharableUri);
		setResult(RESULT_OK, intent);
	}

	private List<String> queryInstalledMobPkg(Context context) {
		List<String> pkgs = new ArrayList<>();
		try {
			List<ProviderInfo> providerInfos = context.getPackageManager().queryContentProviders(null, 0, PackageManager.MATCH_ALL);
			if (providerInfos != null) {
				for (ProviderInfo pri : providerInfos) {
					Log.d(TAG, "Provider info:" + pri);
					String authority = pri.authority;
					// FileProvider 的name格式：${applicationId}.mob_file_provider
					if (!TextUtils.isEmpty(authority) && authority.contains(FILE_PROVIDER_NAME)) {
						// 排除本应用
						String pkg = pri.packageName;
						if (!context.getPackageName().equals(pkg)) {
							pkgs.add(pkg);
						}
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return pkgs;
	}
}
