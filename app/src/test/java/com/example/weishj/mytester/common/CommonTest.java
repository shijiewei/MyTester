package com.example.weishj.mytester.common;

import android.text.TextUtils;

import com.example.weishj.mytester.crypto.Crypto;
import com.mob.tools.utils.Hashon;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;

/**
 * Created by weishj on 2017/5/3.
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({TextUtils.class})
public class CommonTest {

    @Test
    public void testFormatVersionName() throws Exception {
//        PowerMockito.mockStatic(TextUtils.class);
//        PowerMockito.when(TextUtils.class, "isEmpty", anyString()).thenReturn(false);

        Common comon = new Common();
        Class clazz = comon.getClass();
        Method method = clazz.getDeclaredMethod("formatVersionName", String.class);
        method.setAccessible(true);
        String ver = "2.1.4";
        String act = (String)method.invoke(comon, ver);
        Assert.assertEquals("20104", act);
    }

    @Test
    public void testIntToByte() throws Exception {

        Common comon = new Common();
        Class clazz = comon.getClass();
        Method method = clazz.getDeclaredMethod("intToByte", int.class);
        method.setAccessible(true);
        int n = Integer.MIN_VALUE;
        byte[] act = (byte[])method.invoke(comon, n);
        Assert.assertEquals(4, act.length);
    }

    @Test
    public void testByte2Int() throws Exception {

        Common comon = new Common();
        Class clazz = comon.getClass();
        Method method = clazz.getDeclaredMethod("intToByte", int.class);
        method.setAccessible(true);
        int n = Integer.MAX_VALUE;
        byte[] act = (byte[])method.invoke(comon, n);

        Method method2 = clazz.getDeclaredMethod("byte2Int", byte[].class);
        method2.setAccessible(true);
        int m = (int)method2.invoke(comon, act);

        Assert.assertEquals(n, m);
    }

    @Test
    public void testArrayCopy() throws Exception {

        int[] src = {0,1,2,3,4,5,6,7,8,9,10};
        int[] dest = Common.arrayCopy(src, 6, 2);
        int[] expect = {6,7};

        Assert.assertTrue(Arrays.equals(expect, dest));
    }

    @Test
    public void testString() {
        String str = "123";
        Common common = new Common();
        str = common.clearString(str);
        Assert.assertEquals("", str);
    }

    @Test
	public void testModifyMap() {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("key1", "params1");
		paramsMap.put("key2", "params2");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("params", paramsMap);
		Common common = new Common();
		Map<String, Object> modified = common.modifyMap(map);
		for(Map.Entry entry : modified.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	@Test
	public void testArrayList() {
		final List<Integer> list = new ArrayList<>(25);
		final boolean[] doneArr = new boolean[]{false, false, false, false};

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 5; i < 10; i++) {
					list.add(i, i);
					try {
						Thread.sleep(90);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				doneArr[0] = true;
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 10; i < 15; i++) {
					list.add(i, i);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				doneArr[1] = true;
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 15; i < 20; i++) {
					list.add(i, i);
					try {
						Thread.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				doneArr[2] = true;
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 20; i < 25; i++) {
					list.add(i, i);
					try {
						Thread.sleep(70);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				doneArr[3] = true;
			}
		}).start();

		for (int i = 0; i < 5; i++) {
			list.add(i, i);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		out:
		while (true) {
			for (boolean done : doneArr) {
				if (!done) {
					continue out;
				}
			}

			for (Integer e : list) {
				System.out.println("" + e);
			}
			break ;
		}
	}

}
