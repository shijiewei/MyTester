package com.example.weishj.mytester.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static String getDefaultTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		return sdf.format(new Date(timestamp));
	}
}
