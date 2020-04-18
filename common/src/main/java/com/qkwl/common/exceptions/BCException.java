package com.qkwl.common.exceptions;

import java.io.Serializable;

/**
 * 用户自定义异常
 */
public class BCException extends Exception  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public BCException() {
		super( );
	}

	public BCException(String message) {
		super(message);

	}
	public BCException(Throwable innerException){
		super(innerException);
	}
	public BCException(String message,Throwable innerException){
		super(message,innerException);
	}

	public BCException(String message, Object... args) {
		super(String.format(message, args));
	}
}
