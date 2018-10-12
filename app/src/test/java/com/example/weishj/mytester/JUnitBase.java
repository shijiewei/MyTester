package com.example.weishj.mytester;

import java.security.Security;

import org.junit.Before;

/**
 * Created by weishj on 2018/4/8.
 */

public class JUnitBase {
	@Before
	public void setUp() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
}
