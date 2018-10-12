package com.example.weishj.mytester.collection.clone;

import java.io.Serializable;

/**
 * Created by weishj on 2018/4/11.
 */

public class Person implements Cloneable, Serializable {
	private int age;
	private String name;
	public Person(Integer age, String name) {
		super();
		this.age = age;
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return super.toString();
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
