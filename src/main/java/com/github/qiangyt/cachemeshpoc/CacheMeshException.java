package com.github.qiangyt.cachemeshpoc;

public class CacheMeshException extends RuntimeException {

	public CacheMeshException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public CacheMeshException(Throwable cause) {
		super(cause);
	}

	public CacheMeshException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
