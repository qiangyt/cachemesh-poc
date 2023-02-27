package com.github.qiangyt.cachemeshpoc;


public interface RemoteCache extends AutoCloseable {

	public static interface Builder {
		RemoteCache build(String name);
	}

	public static enum ValueStatus {
		Changed, // return value and latest version
		NoChange, // return latest version
		NotFound, // return null value
	}

	public static interface GetSingleResponse {
		ValueStatus status();
		byte[] value();
		long version();
	}

	String name();

	GetSingleResponse getSingle(String key, long version);

	// return version
	long setSingle(String key, byte[] value);

}
