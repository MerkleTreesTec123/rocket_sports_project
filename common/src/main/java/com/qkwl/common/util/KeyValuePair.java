package com.qkwl.common.util;

import java.io.Serializable;

/**
 * Key,Value类
 * @author ZKF
 */
public class KeyValuePair<T extends Object, R extends Object> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T key;
	private R value;

	public KeyValuePair(T key, R value) {
		super();
		this.key = key;
		this.value = value;
	}

	public KeyValuePair() {
		super();
		// TODO Auto-generated constructor stub
	}

	public T getKey() {
		return key;
	}

	public void setKey(T key) {
		this.key = key;
	}

	public R getValue() {
		return value;
	}

	public void setValue(R value) {
		this.value = value;
	}
}
