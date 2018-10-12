package com.example.weishj.mytester.collection.clone;

import java.io.Serializable;

/**
 * Created by weishj on 2018/4/11.
 */

public class Body implements Cloneable, Serializable {
	public Head head;
	public Body(Head head) {this.head = head;}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
