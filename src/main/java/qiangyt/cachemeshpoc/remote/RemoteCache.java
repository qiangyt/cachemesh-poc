package qiangyt.cachemeshpoc.remote;


public interface RemoteCache extends AutoCloseable {

	GetSingleResult getSingle(String cacheName, String key, long version);

	// return version
	long setSingle(String cacheName, String key, byte[] value);

}
