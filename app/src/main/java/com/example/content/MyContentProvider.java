package com.example.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

public class MyContentProvider extends ContentProvider {
	private static final String TAG = "MyContentProvider";
	public static final String CONTENT_PROVIDER_AUTHORITY = "com.example.mytester.provider";

	// Creates a UriMatcher lock.
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		/*
		 * The calls to addURI() go here, for all of the content URI patterns that the provider
		 * should recognize. For this snippet, only the calls for table 3 are shown.
		 */

		/*
		 * Sets the integer value for multiple rows in table 3 to 1. Notice that no wildcard is used
		 * in the path
		 */
		uriMatcher.addURI(CONTENT_PROVIDER_AUTHORITY, "file/*", 1);

		/*
		 * Sets the code for a single row to 2. In this case, the "#" wildcard is
		 * used. "content://com.example.app.provider/table3/3" matches, but
		 * "content://com.example.app.provider/table3 doesn't.
		 */
//		uriMatcher.addURI("com.example.mytester.provider", "table3/#", 2);
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		/*
		 * Choose the table to query and a sort order based on the code returned for the incoming
		 * URI. Here, too, only the statements for table 3 are shown.
		 */
		switch (uriMatcher.match(uri)) {
			// If the incoming URI was for all of table3
			case 1:
				if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
				break;
			// If the incoming URI was for a single row
			case 2:
				/*
				 * Because this URI was for a single row, the _ID value part is
				 * present. Get the last path segment from the URI; this is the _ID value.
				 * Then, append the value to the WHERE clause for the query
				 */
				selection = selection + "_ID = " + uri.getLastPathSegment();
				break;
			default:
				// If the URI is not recognized, you should do some error handling here.
				Log.e(TAG, "Uri not matched. uri: " + uri);
		}
		// call the code to actually do the query
		MatrixCursor matrixCursor = new MatrixCursor(new String[]{});
		return matrixCursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		return null;
	}

	@Override
	public int delete(Uri uri, String s, String[] strings) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
		return 0;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		//由于是应用内的不同进程，故这里把文件放到data/data/包名 目录下
		String path = getContext().getFilesDir().getParent();
		File file = new File(path, uri.getPath().substring(1));
		// ParcelFileDescriptor.MODE_READ_ONLY：只可读
		// ParcelFileDescriptor.MODE_WRITE_ONLY：只可写
		// ParcelFileDescriptor.MODE_READ_WRITE：可读可写
		return ParcelFileDescriptor.open(file,
				ParcelFileDescriptor.MODE_READ_WRITE);
	}
}
