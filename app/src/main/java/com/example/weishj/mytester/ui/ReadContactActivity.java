package com.example.weishj.mytester.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weishj.mytester.BaseActivity;
import com.example.weishj.mytester.R;

import java.util.ArrayList;
import java.util.List;

public class ReadContactActivity extends BaseActivity {
	ArrayAdapter<String> adapter;
	List<String> lists = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_contact);
		ListView lv_list = (ListView) findViewById(R.id.lv_list);
		//创建设置适配器
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lists);
		lv_list.setAdapter(adapter);
		//申请读取联系人权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
			} else {
				readContacts();
			}
		} else {
			readContacts();
		}
	}

	private void readContacts() {
		Cursor cursor = null;
		try {
			cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					//获取联系人姓名
					String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					//获取联系人手机号
					String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					lists.add(displayName + "\n" + number);
				}
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					readContacts();
				} else {
					Toast.makeText(this, "取消授权", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
		}
	}
}
