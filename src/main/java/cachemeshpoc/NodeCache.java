package cachemeshpoc;

public interface NodeCache {

	public static interface Manager extends AutoCloseable {
		NodeCache get(String cacheName);
		NodeCache resolve(String cacheName);
	}

	String getName();

	GetResult<byte[]> getSingle(String key, long versh);

	// return versh
	long putSingle(String key, byte[] bytes);


}
