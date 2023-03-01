package cachemeshpoc.remote;


public interface RemoteCache extends AutoCloseable {

	ResolveSingleResult resolveSingle(String cacheName, String key, long version);

	// return version
	long putSingle(String cacheName, String key, byte[] value);

}
