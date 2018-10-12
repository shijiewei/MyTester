package com.example.weishj.mytester.collection.clone;

import java.io.Serializable;

/**
 * Created by weishj on 2018/4/11.
 */

public class Head implements Cloneable, Serializable {
	public  Face face;
	public Head(Face face){
		this.face = face;
	}
}
