package com.example.weishj.mytester.common;

import android.text.TextUtils;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by weishj on 2017/5/4.
 */

public class Common {
    private String formatVersionName(String varName) {
        StringBuffer sb = new StringBuffer();
        if (!TextUtils.isEmpty(varName)) {
            String[] arr = varName.split("\\.");
            if (arr != null && arr.length > 0) {
                sb.append(arr[0]);
                for(int i = 1; i < arr.length; i ++) {
                    if (!TextUtils.isEmpty(arr[i]) && arr[i].length() < 2) {
                        sb.append("0");
                        sb.append(arr[i]);
                    } else if (!TextUtils.isEmpty(arr[i]) && arr[i].length() >= 2) {
                        sb.append(arr[i]);
                    } else {
                        // Do nothing
                    }
                }
            }
        }
        return sb.toString();
    }

    private byte[] intToByte(int n){
        byte[] b = new byte[4];
        for(int i = 0; i < 4; i ++) {
            b[i]=(byte)(n>>(24-i*8));
        }
        return b;
    }

    private int byte2Int(byte[] data) {
        int targets = data[3] & 0xFF |
                (data[2] & 0xFF) << 8 |
                (data[1] & 0xFF) << 16 |
                (data[0] & 0xFF) << 24;
        return targets;
    }

    public static int[] arrayCopy(int[] src, int srcFrom, int len){
        int[] target = new int[len];
        System.arraycopy(src, srcFrom, target, 0, len);
        return target;
    }

    public String clearString(String str) {
        System.out.println("clearString(). str=" + str);
        str = "";
        return str;
    }

    public Map<String, Object> modifyMap(Map<String, Object> map) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("key1", "data1");
		dataMap.put("key10", "data10");
		Map<String, Object> paramsMap = (Map<String, Object>)map.get("params");
		if (paramsMap == null) {
			paramsMap = new HashMap<String, Object>();
			map.put("params", paramsMap);
		}
		mergeMap(paramsMap, dataMap);
		return map;
	}

	private void mergeMap(Map<String, Object> dest, Map<String, Object> src) {
		dest.putAll(src);
	}
}
