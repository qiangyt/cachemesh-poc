package cachemeshpoc;

public interface NodeCache {

	public static interface Manager extends AutoCloseable {
		void register(NodeCache newCache, boolean mergeWithOld);
		NodeCache get(String cacheName);
		NodeCache resolve(String cacheName);
	}

	String getName();

	GetResult getSingle(String key, long versh);

	// return versh
	long putSingle(String key, byte[] bytes);


}
