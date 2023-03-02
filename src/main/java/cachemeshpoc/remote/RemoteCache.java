package cachemeshpoc.remote;


public interface RemoteCache extends AutoCloseable {

	public static interface Builder {
		RemoteCache build();
	}

	Result resolveSingle(String cacheName, String key, long version);

	// return version
	long putSingle(String cacheName, String key, byte[] value);

}
